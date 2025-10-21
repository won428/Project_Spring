package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.Enum.Student_status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StudentInfoDto {

    // User 주요 필드
    private Long userId;
    private String uName;
    private String email;
    private String phone;
    private String gender;

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

    // 생성자 (User, StatusRecords 객체 받아 필드 할당)
    public StudentInfoDto(User user, StatusRecords statusRecords) {
        if (user != null) {
            this.userId = user.getId();
            this.uName = user.getU_name();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.gender = user.getGender();
            // 전공 등 추가 필드 필요 시 user에서 가져오기
        }

        if (statusRecords != null) {
            this.student_status = statusRecords.getStudent_status();
            this.admissionDate = statusRecords.getAdmissionDate();
            this.leaveDate = statusRecords.getLeaveDate();
            this.returnDate = statusRecords.getReturnDate();
            this.graduationDate = statusRecords.getGraduation_date();
            this.retentionDate = statusRecords.getRetention_date();
            this.expelledDate = statusRecords.getExpelled_date();
        }
    }
}
