package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.dto.StudentInfoDto;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.secondproject.secondproject.Enum.UserType.STUDENT;

@RestController
@RequestMapping("/api/student") // 리액트 호출 경로에 맞춤
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 학생 기본정보 + 학적상태 조회(인증 주체 기준)
    @GetMapping("/info")
    public ResponseEntity<?> getStudentInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "인증 정보가 없습니다."));
        }

        // username은 이메일을 의미(정책 고정)
        String email = authentication.getName();

        // 이메일로 학생 조회
        User user = studentService.getStudentByEmail(email);
        if (user == null || user.getType() != STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생 정보만 조회할 수 있습니다."));
        }

        // 사용자 ID로 학적 상태 조회
        StatusRecords statusRecord = studentService.getStatusRecordByUserId(user.getId());

        // DTO로 응답(프론트가 단일 DTO를 기대하는 경우)
        StudentInfoDto dto = new StudentInfoDto(user, statusRecord);
        return ResponseEntity.ok(dto);
    }

}
