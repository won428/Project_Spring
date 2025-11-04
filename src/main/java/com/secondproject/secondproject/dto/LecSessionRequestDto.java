package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate start; // 강의 시작일

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate end;// 강의 종료일
    private Set<DayOfWeek> days;// 강의 요일
    private Integer periodStart;// 시작 교시
    private Integer periodEnd;// 종료 교시
}
