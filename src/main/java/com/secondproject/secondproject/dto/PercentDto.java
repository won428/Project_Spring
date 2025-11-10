package com.secondproject.secondproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PercentDto {
    BigDecimal attendanceScore;
    BigDecimal assignmentScore;
    BigDecimal midtermExam;
    BigDecimal finalExam;
}
