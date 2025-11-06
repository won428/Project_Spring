package com.secondproject.secondproject.service;

import java.math.BigDecimal;

public record GradingWeightsDto(
        BigDecimal attendanceScore,
        BigDecimal assignmentScore,
        BigDecimal midtermExam,
        BigDecimal finalExam
) { }
