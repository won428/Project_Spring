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

import java.util.HashMap;
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
}
