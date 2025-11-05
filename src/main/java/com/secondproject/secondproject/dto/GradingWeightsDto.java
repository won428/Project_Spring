package com.secondproject.secondproject.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GradingWeightsDto {

    private BigDecimal attendance;

    private BigDecimal assignment;

    private BigDecimal midtermExam;

    private BigDecimal finalExam;
}
