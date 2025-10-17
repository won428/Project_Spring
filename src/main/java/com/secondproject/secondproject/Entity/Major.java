package com.secondproject.secondproject.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "major")
public class Major {
    // 학과 테이블 입니다.

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long major_id; // 학과 코드

    @Column(nullable = false)
    private String m_name; // 학과 이름

    private int m_office; // 학과 전화번호



}
