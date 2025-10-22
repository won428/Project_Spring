package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
public class UserListDto {


    private Long id; // 유저 아이디(PK, number type)

    private Long user_code; // 유저 번호

    private String u_name; // 이름

    private String password; // 비밀번호
    // 비밀번호에 제약조건 안걸려있는 이유는 초기 비밀번호를 학번이나 전화번호로 설정하게 만들고 그 후에 수정할때 제약조건 걸어야합니다.

    private LocalDate birthdate; // 생년월일 (date 타입)

    private String email; // 이메일

    private String phone; // 휴대전화

    private String gender; // 성별 (enum, 문자열 컬럼)

    private String major; // 소속학과ID (number, FK)

    private String college; // 소속 단과대학

    private UserType u_type; // 구분: 학생, 교수, 관리자 (enum)

    public UserListDto(Long id, Long user_code, String u_name, String password, LocalDate birthdate, String email, String phone, String gender, String major, String college, UserType u_type) {
        this.id = id;
        this.user_code = user_code;
        this.u_name = u_name;
        this.password = password;
        this.birthdate = birthdate;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.major = major;
        this.college = college;
        this.u_type = u_type;
    }

    public UserListDto() {
    }
}
