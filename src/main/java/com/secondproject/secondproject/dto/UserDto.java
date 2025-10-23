package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserDto {

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

    public UserDto(String u_name, String password, LocalDate birthdate, String email, String phone, String gender, Long major, UserType u_type) {
        this.u_name = u_name;
        this.password = password;
        this.birthdate = birthdate;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.major = major;
        this.u_type = u_type;
    }

    public UserDto() {
    }
}