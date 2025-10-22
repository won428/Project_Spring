package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member")
public class UserController {

    private final UserService userService;

    // 생성자 주입 (필드 주입 대신, @RequiredArgsConstructor 없이 직접 작성)
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    // 학생 정보 + 학적 정보 조회
//    @GetMapping("/{user_id}")
//    public ResponseEntity<?> getStudentInfo(@PathVariable Long id) {
//        User user = userService.getUserById(id);
//
//        // enum UserType.STUDENT을 직접 비교
//        if (user.getU_type() != UserType.STUDENT) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", "학생 정보만 조회할 수 있습니다."));
//        }
//
//        // RecordStatus 엔티티명도 실제 테이블명과 맞춰 사용
//        StatusRecords statusRecord = userService.getStatusRecordById(null);
//        StudentInfoDto dto = new StudentInfoDto(user, statusRecord);
//        return ResponseEntity.ok(dto);
//    }

    // 사용자 역할에 따른 기능 제한 예시
//    @PostMapping("/{user_id}/request")
//    public ResponseEntity<?> studentFeature(@PathVariable Long id, @RequestBody Map<String, Object> body) {
//        User user = userService.getUserById(id);
//        //
//        switch (user.getU_type()) {
//            case STUDENT:
//                return ResponseEntity.ok(Map.of("message", "학생 기능 수행"));
//            case PROFESSOR:
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "교수는 해당 기능을 사용할 수 없습니다."));
//            case ADMIN:
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "관리자는 학생 전용 기능을 사용할 수 없습니다."));
//            default:
//                return ResponseEntity.badRequest().body(Map.of("error", "알 수 없는 사용자 유형입니다."));
//        }
//    }

//    // 졸업생 기능 제한 및 증명서 발급
//    @GetMapping("/{user_id}/certificate/{type}")
//    public ResponseEntity<?> issueCertificate(@PathVariable Long id, @PathVariable String type) {
//        User user = userService.getUserById(id);
//        if (user.getU_type() != UserType.STUDENT) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", "학생 전용 서비스입니다."));
//        }
//
//        StatusRecords sr = userService.getStatusRecordById(null);
//        boolean isGraduated = "졸업".equals(sr.getStudent_status());
//
//        if (!isGraduated) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", "재학생/휴학생은 증명서 발급 메뉴에서 신청하세요."));
//        }
//
//        if (!List.of("등록금납부내역서", "성적증명서", "장학수혜내역서").contains(type)) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "졸업생만 발급 가능한 증명서 종류입니다."));
//        }
//        return ResponseEntity.ok(userService.issueCertificate(id, type));
//    }
//    // 필요한 기타 기능 및 예외처리 확장 가능
//
//    @PostMapping("/insert")
//    public ResponseEntity<?> insertMember() {
//        return null;
//    }


}