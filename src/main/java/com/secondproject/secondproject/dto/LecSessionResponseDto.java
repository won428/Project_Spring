package com.secondproject.secondproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecSessionResponseDto {
    Long lectureId;
    LocalDate start; // 강의시작일
    LocalDate end; // 강의종료일
    Set<DayOfWeek> days;// 강의 요일
    Integer periodStart;// 강의 시작교시
    Integer periodEnd;// 강의 종료교시
    Integer totalSessions; // 총 차시 수
    List<LecSessionListDto> sessionLists; // 실제 차시 리스트

}
