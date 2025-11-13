package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.Enum.FileType;
import com.secondproject.secondproject.service.AttachmentService;
import com.secondproject.secondproject.service.CreditAppealService;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @ModelAttribute GradeAppealDto appealForm, // Multipart 파일 포함 가능
            @RequestParam("files") List<MultipartFile> files
    ) {
        // 1. 이의제기 저장
        appealService.createGradeAppeal(appealForm);

        // 2. 파일 저장
        if (files != null && !files.isEmpty()) {
            attachmentService.saveFiles(
                    appealForm.getSendingId(),
                    appealForm.getLectureId(), // parentId
                    FileType.APPEAL,
                    files
            );
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
            @RequestBody AttendanceAppealDto dto
    ) {
        appealService.updateAttendanceAppeal(appealId, dto);
        return ResponseEntity.ok().build();
    }

    // appealId로 출결 정보 조회
    @GetMapping("/attendance/{appealId}")
    public ResponseEntity<AttendanceCheckDto> getAttendanceByAppeal(@PathVariable Long appealId) {
        AttendanceCheckDto dto = appealService.getAttendanceByAppeal(appealId);
        return ResponseEntity.ok(dto);
    }
}


// 교수로 로그인 시 성적 이의신청 처리 매핑 필요