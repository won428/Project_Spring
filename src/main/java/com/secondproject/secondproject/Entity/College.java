package com.secondproject.secondproject.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "college")
public class College {
    // 단과대학 엔터티 입니다.

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "college_id")
    private Long id; // 단과대학코드

    @Column(nullable = false)
    private String c_type; // 계열(ENUM으로 바꿀건지 의논해봐야합니다)


    private int c_office; // 행정실 전화번호



}
