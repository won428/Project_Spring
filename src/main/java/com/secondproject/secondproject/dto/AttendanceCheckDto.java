package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.AttendStudent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckDto {
    private Long attendanceId;      // 출결 기록 ID
    private LocalDate attendanceDate; // 강의일
    private AttendStudent attendStudent; // 현재 출결 상태
}