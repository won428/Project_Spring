package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.dto.StudentInfoDto;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student") //
public class StudentController {

    private final StudentService studentService;

    // 생성자 주입
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 학생 정보 + 학적 정보 조회
    @GetMapping("/{user_id}")
    public ResponseEntity<?> getStudentInfo(@PathVariable Long id) {
        User user = studentService.getStudentById(id);

        if (user == null || user.getU_type() != UserType.STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생 정보만 조회할 수 있습니다."));
        }

        StatusRecords statusRecord = studentService.getStatusRecordById(user.getId());
        StudentInfoDto dto = new StudentInfoDto(user, statusRecord);
        return ResponseEntity.ok(dto);
    }

    // 사용자 역할에 따른 기능 제한 예시
    @PostMapping("/{user_id}/request")
    public ResponseEntity<?> studentFeature(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User user = studentService.getStudentById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생 정보가 없습니다."));
        }

        switch (user.getU_type()) {
            case STUDENT:
                return ResponseEntity.ok(Map.of("message", "학생 기능 수행"));
            case PROFESSOR:
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "교수는 해당 기능을 사용할 수 없습니다."));
            case ADMIN:
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "관리자는 학생 전용 기능을 사용할 수 없습니다."));
            default:
                return ResponseEntity.badRequest().body(Map.of("error", "알 수 없는 사용자 유형입니다."));
        }
    }

    // 졸업생 기능 제한 및 증명서 발급
    @GetMapping("/{user_id}/certificate/{type}")
    public ResponseEntity<?> issueCertificate(@PathVariable Long id, @PathVariable String type) {
        User user = studentService.getStudentById(id);

        if (user == null || user.getU_type() != UserType.STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생 전용 서비스입니다."));
        }

        StatusRecords sr = studentService.getStatusRecordById(user.getId());
        boolean isGraduated = "졸업".equals(sr.getStudent_status());

        if (!isGraduated) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "재학생/휴학생은 증명서 발급 메뉴에서 신청하세요."));
        }

        if (!List.of("등록금납부내역서", "성적증명서", "장학수혜내역서").contains(type)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "졸업생만 발급 가능한 증명서 종류입니다."));
        }

        return ResponseEntity.ok(studentService.issueCertificate(id, type));
    }

    /* ====================== [ 교수 전용 기능 ] ====================== */

    @GetMapping("/professor/{professor_id}/students")
    public ResponseEntity<?> getStudentsByProfessor(@PathVariable("professor_id") Long professorId,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) Long studentId) {
        // 1. 교수 계정 확인
        User professor = studentService.getStudentById(professorId);
        if (professor == null || professor.getU_type() != UserType.PROFESSOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "교수 전용 서비스입니다."));
        }

        // 교수로 로그인 시 수업별로 수강중인 학생 리스트 출력
        List<User> students = studentService.getStudentsByLecture(professorId);

        // 필터링
        List<Map<String, Object>> result = new ArrayList<>();
        for (User s : students) {
            if ((name == null || s.getU_name().contains(name)) &&
                    (studentId == null || s.getId().equals(studentId))) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("name", s.getU_name());
                result.add(map);
            }
        }

        if (result.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "조건에 맞는 학생이 없습니다."));
        }

        return ResponseEntity.ok(result);
    }
}
