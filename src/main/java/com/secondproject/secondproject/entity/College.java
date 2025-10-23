package com.secondproject.secondproject.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "college_id")
    private Long id; // 단과대학코드

    @Column(name = "c_type", nullable = false)
    private String type; // 계열(ENUM으로 바꿀건지 의논해봐야합니다)

    @Column(name = "c_office")
    private String office; // 행정실 전화번호



}