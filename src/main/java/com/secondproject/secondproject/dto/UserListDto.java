package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.Gender;
import com.secondproject.secondproject.Enum.UserType;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserListDto {


    private Long id;

    private Long user_code; // 유저 번호

    private String u_name; // 이름

    private String password; // 비밀번호

    private LocalDate birthdate; // 생년월일 (date 타입)

    private String email; // 이메일

    private String phone; // 휴대전화

    private Gender gender; // 성별 (enum, 문자열 컬럼)

    private String major; // 소속학과ID (number, FK)

    private String college; // 소속 단과대학

    private int level;

    private UserType u_type; // 구분: 학생, 교수, 관리자 (enum)


}