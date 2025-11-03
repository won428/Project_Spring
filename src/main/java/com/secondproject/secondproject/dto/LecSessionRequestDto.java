package com.secondproject.secondproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecSessionRequestDto {
    LocalDate start; // 강의 시작일
    LocalDate end;// 강의 종료일
    Set<DayOfWeek> days;// 강의 요일
    Integer periodStart;// 시작 교시
    Integer periodEnd;// 종료 교시
}
