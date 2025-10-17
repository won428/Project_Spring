package com.secondproject.secondproject.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "user")
public class User {
    // 유저 테이블입니다. 유저 테이블에서 학생, 교수, 관리자를 모두 관리합니다. 관리자는 관리자 내에서 분기 처리할지 회의 필요
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id; // 유저 아이디,

    @Column(nullable = false, length = 50)
    private String u_name; // 유저 이름

    @Column(nullable = false)
    private String password; // 유저 비밀번호
    // 비밀번호에 제약조건 안걸려있는 이유는 초기 비밀번호를 학번이나 전화번호로 설정하게 만들고 그 후에 수정할때 제약조건 걸어야합니다.

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate; // 유저 생년월일

    @Email
    @Column(unique = true, nullable = false)
    private String email; // 유저 이메일

    @Column(nullable=false)
    private String gender; // 유저 성별

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="major_id")
    private Major major; // 소속 학과

    @Enumerated(EnumType.STRING)
    private UserType u_type; // 유저 구분 (학생, 교수, 관리자)







}
