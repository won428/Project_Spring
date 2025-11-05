package com.secondproject.secondproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecSessionListDto {
    private LocalDate date; // 강의 일
    private DayOfWeek dayOfWeek; // 강의 요일
    private Integer weekNo; // 학기 n주차
    private Integer periodStart; // 시작교시
    private Integer periodEnd; // 종료교시
    private LocalTime startTime; // 시작시간
    private LocalTime endTime; // 종료시간

    //테스트용 주석
}
