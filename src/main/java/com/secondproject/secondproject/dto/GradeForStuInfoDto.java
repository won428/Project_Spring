package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Status;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GradeForStuInfoDto {
    private Long lecId;

    private Long gradeId;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 강의 시작일

    Status status;

    private BigDecimal aScore = BigDecimal.ZERO; // 출석 점수

    private BigDecimal asScore = BigDecimal.ZERO; // 과제 점수

    private BigDecimal tScore = BigDecimal.ZERO; // 중간 점수

    private BigDecimal ftScore = BigDecimal.ZERO; // 기말 점수

    private BigDecimal totalScore = BigDecimal.ZERO; // 총점

    private BigDecimal lectureGrade; // 학점

}
