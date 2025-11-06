package com.secondproject.secondproject.dto;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class BellTimeRules {
    private static final LocalTime BASE = LocalTime.of(9, 0);   // 1교시 시작
    private static final LocalTime DAY_END = LocalTime.of(18, 0);
    private static final int PERIOD_MIN = 60;   // 교시 길이(분)
    private static final int BREAK_MIN  = 0;    // 쉬는시간(분)
    private static final int UNIT_MIN   = PERIOD_MIN + BREAK_MIN;

    // 하루 가능한 최대 교시 수(= 9교시)
    public static int maxPeriodsPerDay() {
        long total = ChronoUnit.MINUTES.between(BASE, DAY_END);
        return Math.toIntExact(Math.floorDiv(total, UNIT_MIN));
    }

    private static void validatePeriod(int p) {
        int max = maxPeriodsPerDay();
        if (p < 1 || p > max) {
            throw new IllegalArgumentException("period out of range: " + p + " (1.." + max + ")");
        }
    }

    // p교시 시작 시각
    public static LocalTime bellStart(int period) {
        validatePeriod(period);
        return BASE.plusMinutes((long) (period - 1) * UNIT_MIN);
    }

    // p교시 종료 시각 (끝 시각은 '다음 교시 시작'과 동일)
    public static LocalTime bellEnd(int period) {
        validatePeriod(period);
        return bellStart(period).plusMinutes(PERIOD_MIN);
    }
}
