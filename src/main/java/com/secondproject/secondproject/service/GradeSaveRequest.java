package com.secondproject.secondproject.service;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record GradeSaveRequest (
   @NotNull Long userId,
   @NotNull Long lectureId,
   @DecimalMin("0") BigDecimal attendance,
   @DecimalMin("0") @DecimalMax("100") BigDecimal asScore,
   @DecimalMin("0") @DecimalMax("100") BigDecimal tScore,
   @DecimalMin("0") @DecimalMax("100") BigDecimal ftScore
){ }
