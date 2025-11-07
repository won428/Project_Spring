package com.secondproject.secondproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@Table(name = "grade")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grade_user"))
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lecture_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grade_lecture"))
    private Lecture lecture;

    @Column(name = "a_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    @ColumnDefault("0")
    private BigDecimal aScore = BigDecimal.ZERO; // 출석 점수

    @Column(name = "as_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    @ColumnDefault("0")
    private BigDecimal asScore = BigDecimal.ZERO; // 과제 점수

    @Column(name = "t_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    @ColumnDefault("0")
    private BigDecimal tScore = BigDecimal.ZERO; // 중간 점수

    @Column(name = "ft_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    @ColumnDefault("0")
    private BigDecimal ftScore = BigDecimal.ZERO; // 기말 점수

    @Column(name = "total_score", precision = 4, scale = 2, nullable = false) // => DECIMAL(4,2)
    @Digits(integer = 2, fraction = 2)  // Bean Validation (00.00 ~ 99.99)
    @ColumnDefault("0")
    private BigDecimal totalScore = BigDecimal.ZERO; // 총점

    @Column(name = "lecture_grade")
    private String lectureGrade; // 학점

    @Column(name = "lecture_atRate")
    private double atRate; // 학점
}