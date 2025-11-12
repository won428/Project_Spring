package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.dto.GradingInputs;
import com.secondproject.secondproject.dto.GradingResult;
import com.secondproject.secondproject.entity.GradingWeights;
import com.secondproject.secondproject.service.GradingWeightsDto;

public interface GradingCalculator {
GradingResult gradeCalculating(GradingInputs grade, GradingWeightsDto weights);
}
