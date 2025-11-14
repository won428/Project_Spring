package com.secondproject.secondproject.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudentCreditDto {

    // Grade 사항
    private Long id; // 개별 성적 id

    private Long userId; // 학생 id

    private Long lectureId;

    private BigDecimal aScore;

    private BigDecimal asScore;

    private BigDecimal tScore;

    private BigDecimal ftScore;

    private BigDecimal totalScore; // 총점

    private BigDecimal lectureGrade; // 학점

    // Lecture 사항
    private String lecName;
    private LocalDate startDate; // 강의 시작일
}
