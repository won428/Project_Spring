package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.AttendStudent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDto {
    private Long id;

    private Long userId;

    private Long enrollmentId;

    private LocalDate attendanceDate;

    private AttendStudent attendStudent;
}
