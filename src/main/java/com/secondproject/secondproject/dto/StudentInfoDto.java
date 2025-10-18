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
@NoArgsConstructor // 반드시 필요
public class StudentInfoDto {
    // User 주요 필드 (테이블 컬럼명 기준)
    private Long userId;
    private String uName;
    private String email;
    private String phone;
    private String gender;
    private Long majorId;
    private LocalDate birthdate;
    private String uType;
    private Long statusId;

    // RecordStatus 주요 필드
    private Student_status student_status;
    private LocalDate admissionDate;
    private LocalDate leaveDate;
    private LocalDate returnDate;
    private LocalDate graduationDate;
    private LocalDate retentionDate;
    private LocalDate expelledDate;
    private Number totalCredit;
    private Double currentCredit;
    private String studentImage;

    // 매핑용 생성자
    public StudentInfoDto(User user, StatusRecords statusRecords) {
        this.userId = user.getId();             // getId() → getUserId()
        this.uName = user.getU_name();              // getU_name()
        this.email = user.getEmail();             // getEmail() → getU_email()
        this.phone = user.getPhone();             // getPhone() → getU_phone()
        this.gender = user.getGender();             // getGender()
        /* (10/18 수정사항) getMajor() -> getMajor().getId()
        getMajor_id() 요망 시 타 클래스에 Major 객체 내 ID를 직접 반환하는 메서드 구현 필요
         */
        this.majorId = user.getMajor();          // getMajor() → getMajor_id()
        this.birthdate = user.getBirthdate();       // LocalDate 타입 그대로
        this.uType = user.getU_type().name();       // getUType() → getU_type()
        this.statusId = user.getStatus_id();        // getStatusId() → getStatus_id()
        // RecordStatus 정보도 아래에 추가

        if (statusRecords != null) {
            this.student_status = statusRecords.getStudent_status();
            this.admissionDate = statusRecords.getAdmissionDate();
            this.leaveDate = statusRecords.getLeaveDate();
            this.returnDate = statusRecords.getReturnDate();            // 반환일 누락된 부분 추가
            this.graduationDate = statusRecords.getGraduation_date();   // 필드명과 Getter 명 주의:
            this.retentionDate = statusRecords.getRetention_date();     // 필드명 retention_date
            this.expelledDate = statusRecords.getExpelled_date();       // 필드명 expelled_date
            this.totalCredit = statusRecords.getTotalCredit();
            this.currentCredit = statusRecords.getCurrentCredit();
            this.studentImage = statusRecords.getStudent_image();       // 필드명 student_image
        }
    }
}