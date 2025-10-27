package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    // 생성: React ChangeStatusPage (POST /api/student/record)
    @PostMapping("/record")
    public ResponseEntity<StatusChangeRequestDto> applyStatusChange(@RequestBody StatusChangeRequestDto dto) {
        StatusChangeRequestDto created = statusService.createChangeRequest(dto);
        return ResponseEntity.ok(created);
    }

    // 목록: React ChangeStatusList (GET /api/student/records/{userId})
    @GetMapping("/records/{userId}")
    public ResponseEntity<List<StatusChangeRequestDto>> getStudentChangeRequests(@PathVariable Long userId) {
        List<StatusChangeRequestDto> list = statusService.getStudentChangeRequests(userId);
        return ResponseEntity.ok(list);
    }

    // 상세: React ChangeStatusDetail (GET /api/student/record/{recordId})
    @GetMapping("/record/{recordId}")
    public ResponseEntity<StatusChangeRequestDto> getChangeRequestDetail(@PathVariable Long recordId) {
        StatusChangeRequestDto detail = statusService.getChangeRequestDetail(recordId);
        if (detail == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(detail);
    }

    // 동시 업로드: 파일 + DTO 한 번에 전송 (POST /api/student/attachments)
//    @PostMapping(value = "/attachments", consumes = {"multipart/form-data"})
//    public ResponseEntity<StatusChangeRequestDto> applyStatusChangeWithFile(
//            @RequestPart("dto") StatusChangeRequestDto dto,
//            @RequestPart(value = "file", required = false) MultipartFile file
//    ) {
//        Long attachmentId = (file != null && !file.isEmpty())
//                ? statusService.storeAttachmentFile(file) : null;
//        if (attachmentId != null) dto.setAttachmentId(attachmentId);
//        StatusChangeRequestDto created = statusService.createChangeRequest(dto);
//        return ResponseEntity.ok(created);
//    }
}
