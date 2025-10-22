package com.secondproject.secondproject.Controller;

import com.secondproject.secondproject.Entity.StudentRecord;
import com.secondproject.secondproject.Dto.StatusChangeRequestDto;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.Service.StatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/student_record")
public class StatusController {

    @Autowired
    private StatusService statusService;

    // 학적 변경 신청 API - 학생만 가능하도록 제약조건 유지
    @PostMapping("/change")
    public ResponseEntity<String> applyStatusChange(@RequestPart("dto") StatusChangeRequestDto dto,
                                                    @RequestPart(value = "file", required = false) MultipartFile file,
                                                    @RequestParam("userType") UserType userType) {
        if (userType != UserType.STUDENT) {
            return ResponseEntity.status(403).body("학적 변경 신청은 학생만 가능합니다.");
        }
        if (file != null && !file.isEmpty()) {
            statusService.storeAttachmentFile(file);
        }
        statusService.changeStatusWithEvidence(dto);
        return ResponseEntity.ok("학적 변경 신청이 완료되었습니다.");
    }

    // 특정 학생의 학적 변경 신청 상태 목록 조회 API
    @GetMapping("/change_requests/{applier}")
    public ResponseEntity<List<StudentRecord>> getStudentChangeRequestsStatus(@PathVariable Long applier) {
        List<StudentRecord> records = statusService.getStudentChangeRequestsStatus(applier);
        return ResponseEntity.ok(records);
    }

    // *** 신규 추가: 개별 학적 변경 신청 상세내역 조회 API ***
    @GetMapping("/change_request/{recordId}")
    public ResponseEntity<StatusChangeRequestDto> getChangeRequestDetail(@PathVariable Long recordId) {
        StatusChangeRequestDto detailDto = statusService.getChangeRequestDetail(recordId);

        if (detailDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(detailDto);
    }
}
