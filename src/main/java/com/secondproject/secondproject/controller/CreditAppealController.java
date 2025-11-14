package com.secondproject.secondproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondproject.secondproject.Enum.AttendStudent;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.Enum.FileType;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.Mapping.AppealAttach;
import com.secondproject.secondproject.service.AttachmentService;
import com.secondproject.secondproject.service.CreditAppealService;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class CreditAppealController {

    private final CreditAppealService appealService;
    private final AttachmentService attachmentService;
    private final LectureService lectureService;
    private final UserService userService;

    // GET /api/enrollments?userId=1
    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentInfoDto>> getEnrollments(@RequestParam Long userId) {
        List<EnrollmentInfoDto> enrollments = appealService.findEnrollmentsByUserId(userId);
        return ResponseEntity.ok(enrollments);
    }

    // POST /api/appeals
    @PostMapping("/myappeal")
    public ResponseEntity<?> createAppeal(
            @ModelAttribute GradeAppealDto appealForm,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        // 1. 이의제기 생성
        Long appealId = appealService.createGradeAppeal(appealForm); // ID 반환 받기

        // 2. 새 파일 업로드 및 AppealAttach 매핑
        if (files != null && !files.isEmpty()) {
            List<Attachment> savedFiles = attachmentService.saveFiles(
                    appealForm.getSendingId(),
                    appealForm.getLectureId(),
                    FileType.APPEAL,
                    files
            );

            // AppealAttach 생성
            appealService.mapAttachmentsToAppeal(appealId, savedFiles);
        }

        return ResponseEntity.ok(200);
    }

    // 교수 이름 찾아오기
    @GetMapping("/users/{id}")
    public ResponseEntity<UserNameDto> getUserName(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserNameById(id));
    }

    @GetMapping("/lectures/{lectureId}")
    public ResponseEntity<LectureBasicInfoDto> getLectureBasicInfo(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLectureBasicInfo(lectureId));
    }

    @GetMapping("/mylist")
    public ResponseEntity<List<AppealListDto>> getMyAppeals(@RequestParam Long id) {
        return ResponseEntity.ok(appealService.getAppealsByStudentId(id));
    }

    @GetMapping("/grades/one")
    public StudentCreditDto getGrade(
            @RequestParam Long userId,
            @RequestParam Long lectureId
    ) {
        return appealService.getGradeForStudent(userId, lectureId);
    }

    // 강의별 이의제기 조회
    @GetMapping("/lectureAppeals/{lectureId}")
    public ResponseEntity<List<AppealManageDto>> getAppealsByLecture(
            @PathVariable Long lectureId,
            @RequestParam Long receiverId
    ) {
        List<AppealManageDto> appeals = appealService.getAppealsByLecture(lectureId, receiverId);
        return ResponseEntity.ok(appeals);
    }

    // 승인
    @PutMapping("/{appealId}/approve")
    public ResponseEntity<Void> approveAppeal(
            @PathVariable Long appealId,
            @RequestBody AppealManageDto dto
    ) {
        appealService.approveAppeal(appealId, dto);
        return ResponseEntity.ok().build();
    }

    // 반려
    @PutMapping("/{appealId}/reject")
    public ResponseEntity<Void> rejectAppeal(@PathVariable Long appealId) {
        appealService.rejectAppeal(appealId);
        return ResponseEntity.ok().build();
    }

    // 성적 수정
    @PutMapping("/{appealId}/updateScores")
    public ResponseEntity<Void> updateScores(
            @PathVariable Long appealId,
            @RequestBody UpdateScoresDto dto
    ) {
        appealService.updateScores(appealId, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{appealId}/updateStatus")
    public ResponseEntity<Void> updateAttendanceStatus(
            @PathVariable Long appealId,
            @RequestParam AttendStudent newStatus,
            @RequestParam LocalDate attendanceDate,
            @RequestParam Long sendingId,
            @RequestParam Long receiverId,
            @RequestParam Long lectureId,
            @RequestParam Status status
            ) {
        AttendanceAppealDto dto = new AttendanceAppealDto();
        dto.setAppealId(appealId);
        dto.setAttendStudent(newStatus);
        dto.setSendingId(sendingId);
        dto.setReceiverId(receiverId);
        dto.setLectureId(lectureId);
        dto.setAttendanceDate(attendanceDate);
        dto.setStatus(status);
        this.appealService.updateAttendanceAppeal(appealId, dto);
        System.out.println(dto.getAttendStudent());
        return ResponseEntity.ok().build();
    }

    // appealId로 출결 정보 조회
    @GetMapping("/attendance/{appealId}")
    public ResponseEntity<AttendanceCheckDto> getAttendanceByAppeal(@PathVariable Long appealId) {
        AttendanceCheckDto dto = appealService.getAttendanceByAppeal(appealId);
        return ResponseEntity.ok(dto);
    }

    // 특정 Appeal에 연결된 첨부파일 리스트 조회
    @GetMapping("/{appealId}/attachments")
    public ResponseEntity<List<AttachmentDto>> getAppealAttachments(@PathVariable Long appealId) {
        List<AttachmentDto> attachments = appealService.getAttachmentsByAppealId(appealId);
        return ResponseEntity.ok(attachments);
    }

    // 다운로드는 AttachmentService 기존 방식 그대로
    @GetMapping("/files/download/{id}")
    public ResponseEntity<Resource> downloadAppealFile(@PathVariable Long id) {
        DownloadFile downloadFile = attachmentService.downloadFile(id);

        InputStreamResource resource = new InputStreamResource(downloadFile.getInputStream());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadFile.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, downloadFile.getContentType())
                .body(resource);
    }
}


// 교수로 로그인 시 성적 이의신청 처리 매핑 필요