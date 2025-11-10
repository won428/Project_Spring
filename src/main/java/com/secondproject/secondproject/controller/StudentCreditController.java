package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.StudentCreditDto;
import com.secondproject.secondproject.service.StudentCreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class StudentCreditController {

    private final StudentCreditService studentCreditService;

    // 특정 학생(userId), 연도(year), 학기(semester)에 따른 성적 목록 조회
    // userId 기준으로 모든 강의 및 성적 목록 반환
    @GetMapping("/all")
    public ResponseEntity<List<StudentCreditDto>> getGrades(@RequestParam Long userId) {
        List<StudentCreditDto> gradeList = studentCreditService.findByUserId(userId);
        return ResponseEntity.ok(gradeList);
    }


    // 특정 학생(userId) 강의 전체 조회 (년도, 학기 구분은 프론트엔드에서 처리)
    @GetMapping("/semester/lectures")
    public ResponseEntity<List<StudentCreditDto>> getLecturesByUser(@RequestParam Long userId) {
        List<StudentCreditDto> lectureList = studentCreditService.findByUserId(userId);
        return ResponseEntity.ok(lectureList);
    }

    // 필요시 등록/수정 기능은 생략 가능
}
