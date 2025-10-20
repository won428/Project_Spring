package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.dto.StudentInfoDto;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member") // 공통 경로
public class MemberController {

    private final UserService userService;

    public MemberController(UserService userService) {
        this.userService = userService;
    }

    // 학생 정보만 조회 가능 (경로: /member/student/{user_id})
    @GetMapping("/student/{user_id}")
    public ResponseEntity<?> getStudentInfo(@PathVariable Long user_id) {
        User user = userService.getUserById(user_id);

        if (user.getU_type() != UserType.STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생 정보만 조회할 수 있습니다."));
        }

        StatusRecords statusRecord = userService.getStatusRecordById(user.getStatus_id());
        StudentInfoDto dto = new StudentInfoDto(user, statusRecord);
        return ResponseEntity.ok(dto);
    }

    // 학적 변경 신청은 학생에 한함 (경로: /member/student/{user_id}/request)
    @PostMapping("/student/{user_id}/request")
    public ResponseEntity<?> studentFeature(@PathVariable Long user_id, @RequestBody Map<String, Object> body) {
        User user = userService.getUserById(user_id);

        if (user.getU_type() != UserType.STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생만 학적 변경 신청이 가능합니다."));
        }

        // 실제 처리는 서비스에 위임
        return ResponseEntity.ok(Map.of("message", "학적 변경 신청이 처리되었습니다."));
    }

    // 졸업생 증명서 발급 기능 (경로: /member/student/{user_id}/certificate/{type})
    @GetMapping("/student/{user_id}/certificate/{type}")
    public ResponseEntity<?> issueCertificate(@PathVariable Long user_id, @PathVariable String type) {
        User user = userService.getUserById(user_id);

        if (user.getU_type() != UserType.STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생 전용 서비스입니다."));
        }

        StatusRecords sr = userService.getStatusRecordById(user.getStatus_id());
        boolean isGraduated = "졸업".equals(sr.getStudent_status());

        if (!isGraduated) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "재학생/휴학생은 증명서 발급 메뉴에서 신청하세요."));
        }

        if (!List.of("등록금납부내역서", "성적증명서", "장학수혜내역서").contains(type)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "졸업생만 발급 가능한 증명서 종류입니다."));
        }
        return ResponseEntity.ok(userService.issueCertificate(user_id, type));
    }
}
