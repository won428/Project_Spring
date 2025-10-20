package com.secondproject.secondproject.dto;

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


    // 매핑용 생성자
    public StudentInfoDto(User user) {
        this.userId = user.getId();             // getId() → getUserId()
        this.uName = user.getU_name();              // getU_name()
        this.email = user.getEmail();             // getEmail() → getU_email()
//        this.phone = user.getPhone();             // getPhone() → getU_phone()
//        this.gender = user.getGender();             // getGender()
//        this.majorId = user.getMajor().getId();         // getMajor() → getMajor_id()
//        this.birthdate = user.getBirthdate();       // LocalDate 타입 그대로
        this.uType = user.getU_type().name();       // getUType() → getU_type()
        // RecordStatus 정보도 아래에 추가


    }
}