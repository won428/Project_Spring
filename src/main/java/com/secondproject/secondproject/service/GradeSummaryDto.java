package com.secondproject.secondproject.service;

import java.math.BigDecimal;

public record GradeSummaryDto(Long userId, BigDecimal totalScore, BigDecimal lectureGrade) {
}
