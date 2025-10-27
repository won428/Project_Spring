package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.entity.User;
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
    private Long id;          // 사용자 기본키
    private Long userCode;    // 학번
    private String name;      // 이름
    private String email;     // 이메일
    private String password; // 비밀번호
    private String gender;    // 성별
    private LocalDate birthDate; // 생일
    private String phone;     // 휴대전화
    private Major major; // 소속 학과명
    private UserType type; // 구분자

    // User 소속 학과 등 필요한 필드 추가
    // 예) 전공명 majorName; 필요시 추가

    // 학적 정보 (StatusRecords 필드)
    private Long statusId;
    private Student_status student_status;
    private LocalDate admissionDate;
    private LocalDate leaveDate;
    private LocalDate returnDate;
    private LocalDate graduationDate;
    private LocalDate retentionDate;
    private LocalDate expelledDate;
    private int totalCredit;
    private double currentCredit;
    private String studentImage;

    public StudentInfoDto(User user, StatusRecords statusRecord) {

            // User 기반 필드 세팅
            this.id = user.getId();
            this.userCode = user.getUserCode();
            this.name = user.getName();
            this.email = user.getEmail();
            this.password = user.getPassword();
            this.gender = user.getGender();
            this.birthDate = user.getBirthDate();
            this.phone = user.getPhone();
            this.major = user.getMajor();
            this.type = user.getType();

            // StatusRecords 기반 필드 세팅 (null 안전)
            if (statusRecord != null) {
                this.statusId = statusRecord.getId();
                this.student_status = statusRecord.getStudentStatus(); // Enum
                this.admissionDate = statusRecord.getAdmissionDate();
                this.leaveDate = statusRecord.getLeaveDate();
                this.returnDate = statusRecord.getReturnDate();
                this.graduationDate = statusRecord.getGraduationDate();
                this.retentionDate = statusRecord.getRetentionDate();
                this.expelledDate = statusRecord.getExpelledDate();
                this.totalCredit = statusRecord.getTotalCredit();
                this.currentCredit = statusRecord.getCurrentCredit();
                this.studentImage = statusRecord.getStudentImage();
            }
        }
    }

