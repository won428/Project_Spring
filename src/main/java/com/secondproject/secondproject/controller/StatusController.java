package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.service.StatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student-record")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @PostMapping("/change")
    public ResponseEntity<String> applyStatusChange(@RequestBody StatusChangeRequestDto dto) {
        // 학생 권한 체크
        if (dto.getUserType() != UserType.STUDENT) {
            return ResponseEntity.status(403).body("학적 변경 신청은 학생만 가능합니다.");
        }
        statusService.changeStatusWithEvidence(dto);
        return ResponseEntity.ok("학적 변경 신청이 완료되었습니다.");
    }
}