package com.secondproject.secondproject.service;

import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record GradingWeightsDto(
        BigDecimal attendanceScore,
        BigDecimal assignmentScore,
        BigDecimal midtermExam,
        BigDecimal finalExam
) {
}
