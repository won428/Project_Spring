package com.secondproject.secondproject.entity;

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
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // 유저 아이디(PK, number type)

    @Column(name = "user_code", unique = true) // 단일 컬럼 고유
    private Long userCode;

    @Column(name = "u_name", nullable = false, length = 50)
    private String name; // 이름

    @Column(nullable = false)
    private String password; // 비밀번호
    // 비밀번호에 제약조건 안걸려있는 이유는 초기 비밀번호를 학번이나 전화번호로 설정하게 만들고 그 후에 수정할때 제약조건 걸어야합니다.

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate; // 생년월일 (date 타입)

    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email; // 이메일

    @Column(unique = true, nullable = false, length = 20)
    private String phone; // 휴대전화

    @Column(nullable = false)
    private String gender; // 성별 (enum, 문자열 컬럼)

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="major_id", foreignKey = @ForeignKey(name = "fk_user_major"))
    private Major major; // 소속학과ID (number, FK)

    @Enumerated(EnumType.STRING)
    @Column(name = "u_type", nullable = false)
    private UserType type; // 구분: 학생, 교수, 관리자 (enum)


    // 필요시 생성자, equals/hashCode 등 추가 가능
}