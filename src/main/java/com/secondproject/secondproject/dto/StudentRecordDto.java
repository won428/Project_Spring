package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentRecordDto {
    private Long id;

    @Enumerated(EnumType.STRING)
    private Student_status applyStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appliedDate;        // 신청일(date)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate processedDate;      // 처리일(date), null 허용

    @Enumerated(EnumType.STRING)
    private Status status;         // 처리상태

}
