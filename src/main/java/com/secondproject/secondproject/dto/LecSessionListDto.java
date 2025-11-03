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
    LocalDate date; // 강의 일
    DayOfWeek dayOfWeek; // 강의 요일
    Integer weekNo; // 학기 n주차
    Integer periodStart; // 시작교시
    Integer periodEnd; // 종료교시
    LocalTime startTime; // 시작시간
    LocalTime endTime; // 종료시간
}
