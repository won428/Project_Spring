package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Gender;
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
    private String u_name; // 이름
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate; // 생년월일 (date 타입)
    private String email; // 이메일
    private String phone; // 휴대전화
    private Gender gender;
    private String college;
    private Major major; // 소속학과ID (number, FK)
    private Long usercode; // 구분: 학생, 교수, 관리자 (enum)

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
    private int majorCredit;
    private int generalCredit;
    private int totalCredit;
    private double currentCredit;
    private String studentImage;

    public StudentInfoDto(User user, StatusRecords statusRecord) {

            if(user != null) {
                // User 기반 필드 세팅
                this.id = user.getId();
                this.userCode = user.getUserCode();
                this.name = user.getName();
                this.email = user.getEmail();
                this.password = user.getPassword();
                this.gender = user.getGender();
                this.birthDate = user.getBirthDate();
                this.phone = user.getPhone();
                this.major = user.getMajor() != null ? user.getMajor().getName() : null;
                this.type = user.getType();
            }

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
                this.majorCredit = statusRecord.getMajorCredit();
                this.generalCredit = statusRecord.getGeneralCredit();
                this.totalCredit = statusRecord.getTotalCredit();
                this.currentCredit = statusRecord.getCurrentCredit();
                this.studentImage = statusRecord.getStudentImage();
            }
        }
    }

