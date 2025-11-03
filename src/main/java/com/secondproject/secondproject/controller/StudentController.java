package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.dto.StudentInfoDto;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.secondproject.secondproject.Enum.UserType.*;

@RestController
@RequestMapping("/student") //
public class StudentController {

    private final StudentService studentService;

    // 생성자 주입
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 학생 정보 + 학적 정보 조회
    @GetMapping("/info")
    public ResponseEntity<?> getStudentInfo(Authentication authentication) {
        // 1) 인증 여부 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "인증 정보가 없습니다."));
        }

        // 2) 이메일(=username)로 사용자 조회
        String email = authentication.getName();
        User user = studentService.getStudentByEmail(email);

        // 3) STUDENT 타입만 접근 허용
        if (user == null || user.getType() != STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생 정보만 조회할 수 있습니다."));
        }

        // 4) 학적 상태 조회
        StatusRecords statusRecord = studentService.getStatusRecordByUserId(user.getId());

        // 5) DTO 생성(생성자에서 모든 필드 매핑)
        StudentInfoDto dto = new StudentInfoDto(user, statusRecord);

        // 6) 프론트 기대 형태로 감싸서 반환
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("statusid", dto.getStatusId());
        statusMap.put("studentStatus", dto.getStudent_status());
        statusMap.put("admissionDate", dto.getAdmissionDate());
        statusMap.put("leaveDate", dto.getLeaveDate());
        statusMap.put("returnDate", dto.getReturnDate());
        statusMap.put("graduationDate", dto.getGraduationDate());
        statusMap.put("retentionDate", dto.getRetentionDate());
        statusMap.put("expelledDate", dto.getExpelledDate());
        statusMap.put("majorCredit", dto.getMajorCredit());
        statusMap.put("generalCredit", dto.getGeneralCredit());
        statusMap.put("totalCredit", dto.getTotalCredit());
        statusMap.put("currentCredit", dto.getCurrentCredit());
        statusMap.put("studentImage", dto.getStudentImage());

        Map<String, Object> studentMap = new HashMap<>();
        studentMap.put("userid", dto.getId());
        studentMap.put("userCode", dto.getUserCode());
        studentMap.put("name", dto.getName());
        studentMap.put("password", dto.getPassword());
        studentMap.put("birthDate", dto.getBirthDate());
        studentMap.put("email", dto.getEmail());
        studentMap.put("phone", dto.getPhone());
        studentMap.put("gender", dto.getGender());
        // major는 엔티티 전체 직렬화 시 순환 참조 위험이 있을 수 있으므로 필요한 필드만 노출 권장
        // 예: 학과명만 사용한다면 아래처럼 가공
        studentMap.put("major", dto.getMajor()); // 필요 시 Map.of("name", dto.getMajor().getName()) 로 축소
        // type은 Enum → 문자열로 직렬화되므로 프론트 비교가 가능
        studentMap.put("type", dto.getType());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("type", dto.getType()); // "STUDENT"
        responseBody.put("studentInfo", studentMap);
        responseBody.put("statusRecords", statusMap);

        return ResponseEntity.ok(responseBody);
    }

//    // 사용자 역할에 따른 기능 제한 예시
//    @PostMapping("/{user_id}/request")
//    public ResponseEntity<?> studentFeature(@PathVariable Long id, @RequestBody Map<String, Object> body) {
//        User user = studentService.getStudentById(id);
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", "학생 정보가 없습니다."));
//        }
//
//        switch (user.getType()) {
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
//
//    // 졸업생 기능 제한 및 증명서 발급
//    @GetMapping("/{user_id}/certificate/{type}")
//    public ResponseEntity<?> issueCertificate(@PathVariable Long id, @PathVariable String type) {
//        User user = studentService.getStudentById(id);
//
//        if (user == null || user.getType() != STUDENT) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", "학생 전용 서비스입니다."));
//        }
//
//        StatusRecords sr = studentService.getStatusRecordById(user.getId());
//        boolean isGraduated = "졸업".equals(sr.getStudentStatus());
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
//
//        return ResponseEntity.ok(studentService.issueCertificate(id, type));
//    }
//
//    /* ====================== [ 교수 전용 기능 ] ====================== */
//
//    @GetMapping("/professor/{professor_id}/students")
//    public ResponseEntity<?> getStudentsByProfessor(@PathVariable("professor_id") Long professorId,
//                                                    @RequestParam(required = false) String name,
//                                                    @RequestParam(required = false) Long studentId) {
//        // 1. 교수 계정 확인
//        User professor = studentService.getStudentById(professorId);
//        if (professor == null || professor.getType() != PROFESSOR) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", "교수 전용 서비스입니다."));
//        }
//
//        // 교수로 로그인 시 수업별로 수강중인 학생 리스트 출력
//        List<User> students = studentService.getStudentsByLecture(professorId);
//
//        // 필터링
//        List<Map<String, Object>> result = new ArrayList<>();
//        for (User s : students) {
//            if ((name == null || s.getName().contains(name)) &&
//                    (studentId == null || s.getId().equals(studentId))) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("id", s.getId());
//                map.put("name", s.getName());
//                result.add(map);
//            }
//        }
//
//
//        if (result.isEmpty()) {
//            return ResponseEntity.ok(Map.of("message", "조건에 맞는 학생이 없습니다."));
//        }
//
//        return ResponseEntity.ok(result);
//    }
}
