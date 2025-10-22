package com.secondproject.secondproject.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.Enum.Student_status;
import com.secondproject.secondproject.Enum.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentInfoDto {

    // User 주요 필드
    private String u_name; // 이름
    private String password; // 비밀번호
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate; // 생년월일 (date 타입)
    private String email; // 이메일
    private String phone; // 휴대전화
    private String gender;
    private String college;
    private Long major; // 소속학과ID (number, FK)
    private UserType u_type; // 구분: 학생, 교수, 관리자 (enum)

    // User 소속 학과 등 필요한 필드 추가
    // 예) 전공명 majorName; 필요시 추가

    // 학적 정보 (StatusRecords 필드)
    private Student_status student_status;
    private LocalDate admissionDate;
    private LocalDate leaveDate;
    private LocalDate returnDate;
    private LocalDate graduationDate;
    private LocalDate retentionDate;
    private LocalDate expelledDate;

    public StudentInfoDto(User user, StatusRecords statusRecord) {
    }
}
