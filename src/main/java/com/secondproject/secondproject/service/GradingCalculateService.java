package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.GradingInputs;
import com.secondproject.secondproject.dto.GradingResult;
import com.secondproject.secondproject.entity.GradingWeights;
import com.secondproject.secondproject.repository.GradingCalculator;
import com.secondproject.secondproject.repository.GradingWeightsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class GradingCalculateService implements GradingCalculator {
    private static GradingWeightsRepository gradingWeightsRepository;

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final RoundingMode RM = RoundingMode.HALF_UP;


    @Override
    public GradingResult gradeCalculating(GradingInputs grade, GradingWeightsDto weights) {

        final BigDecimal ATT_MAX = weights.attendanceScore();
        final BigDecimal AS_MAX  = weights.assignmentScore();
        final BigDecimal MID_MAX = weights.midtermExam();
        final BigDecimal FIN_MAX = weights.finalExam();

        // 가중치 합이 100인지 확인
        BigDecimal attW = weights.attendanceScore();
        BigDecimal asW = weights.assignmentScore();
        BigDecimal mtW = weights.midtermExam();
        BigDecimal ftW = weights.finalExam();

        BigDecimal sum = attW.add(asW).add(mtW).add(ftW);

        if (sum.compareTo(ONE_HUNDRED) != 0){
            throw new IllegalArgumentException("가중치의 합은 100이어야 합니다 : " + sum);
        }

        // 2) 원점수(0~100)를 BigDecimal로 변환 (double → BigDecimal은 valueOf 권장)
        BigDecimal att = grade.attendance();
        BigDecimal as  = grade.assignment();
        BigDecimal mid = grade.midterm();
        BigDecimal fin = grade.fin();

        // 1) 각 항목을 [0~1]로 정규화
        BigDecimal attPct = att.divide(ATT_MAX, 8, RM);
        BigDecimal asPct  = as.divide(AS_MAX,  8, RM);
        BigDecimal midPct = mid.divide(MID_MAX,8, RM);
        BigDecimal finPct = fin.divide(FIN_MAX,8, RM);

        // 2) 가중치(%)를 곱해 기여점수(0~각 가중치%)를 만들고 합산 → 최종 [0~100]
        BigDecimal total = attPct.multiply(attW)
                .add(asPct.multiply(asW))
                .add(midPct.multiply(mtW))
                .add(finPct.multiply(ftW))
                .setScale(2, RM); // 소수 2자리

        // 3) GPA(4.5 스케일)
        BigDecimal gpa = total
                .divide(ONE_HUNDRED, 4, RM) // 0~1
                .multiply(new BigDecimal("4.5"))
                .setScale(2, RM);

        return new GradingResult(total, gpa);
    }
}
