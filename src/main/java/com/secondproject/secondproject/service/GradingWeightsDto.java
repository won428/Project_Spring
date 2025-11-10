package com.secondproject.secondproject.service;

import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public record GradingWeightsDto(
        BigDecimal attendanceScore,
        BigDecimal assignmentScore,
        BigDecimal midtermExam,
        BigDecimal finalExam
) {
}
