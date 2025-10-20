package com.secondproject.secondproject.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id; // 유저 아이디(PK, number type)

    @Column(nullable = false, length = 50)
    private String u_name; // 이름

    @Column(nullable = false)
    private String password; // 비밀번호
    // 비밀번호에 제약조건 안걸려있는 이유는 초기 비밀번호를 학번이나 전화번호로 설정하게 만들고 그 후에 수정할때 제약조건 걸어야합니다.

    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email; // 이메일


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType u_type; // 구분: 학생, 교수, 관리자 (enum)
    
    // 필요시 생성자, equals/hashCode 등 추가 가능
}
