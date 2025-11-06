package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.AttendStudent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long enrollmentId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @NotNull
    private AttendStudent attendStudent;
}
