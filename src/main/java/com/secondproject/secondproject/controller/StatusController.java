package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.service.StatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/student-record")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @PostMapping("/change")
    public ResponseEntity<String> applyStatusChange(@RequestPart("dto") StatusChangeRequestDto dto,
                                                    @RequestPart(value = "file", required = false) MultipartFile file) {
        if (dto.getUserType() != UserType.STUDENT) {
            return ResponseEntity.status(403).body("학적 변경 신청은 학생만 가능합니다.");
        }

        // 파일이 업로드 되면 저장만 하고 경로 등은 Attachment 엔티티에서 관리
        if (file != null && !file.isEmpty()) {
            statusService.storeAttachmentFile(file); // 파일 저장 처리, 엔티티와 매핑은 서비스에서 별도 관리
        }

        statusService.changeStatusWithEvidence(dto);
        return ResponseEntity.ok("학적 변경 신청이 완료되었습니다.");
    }
}