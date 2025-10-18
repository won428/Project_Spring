package com.secondproject.secondproject.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@Table(name = "grade")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "grade_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(name = "a_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    private BigDecimal a_score; // 출석 점수

    @Column(name = "as_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    private BigDecimal as_score; // 과제 점수

    @Column(name = "t_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    private BigDecimal t_score; // 중간 점수

    @Column(name = "ft_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    private BigDecimal ft_score; // 기말 점수

    @Column(name = "total_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    private BigDecimal total_score; // 총점

    private String lecture_grade; // 학점
}
