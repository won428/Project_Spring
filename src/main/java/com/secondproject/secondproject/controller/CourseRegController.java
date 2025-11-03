package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.entity.CourseRegistration;
import com.secondproject.secondproject.service.CourseRegService;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/courseReg")
@RequiredArgsConstructor
public class CourseRegController {

    private final CourseRegService courseRegService;


    // 장바구니 확정 버튼
    @PutMapping("/applyStatus")
    public ResponseEntity<?> applyStatus(@RequestParam Long id, @RequestParam("status") Status status){
        if (id == null || id < 0 || status == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "존재하지 않는 강의이거나 존재하지 않는 상태 표시입니다."));
        }

        this.courseRegService.updateStatus(id, status);

        return ResponseEntity.ok(200);
    }
}
