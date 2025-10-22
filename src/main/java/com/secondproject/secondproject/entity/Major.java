package com.secondproject.secondproject.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "major_id")
    private Long id; // 학과 코드

    @Column(name = "m_name", nullable = false)
    private String name; // 학과 이름

    @Column(name = "m_office")
    private String mOffice; // 학과 전화번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", foreignKey = @ForeignKey(name = "fk_major_college"))
    private College college; // 소속 단과대학 코드



}
