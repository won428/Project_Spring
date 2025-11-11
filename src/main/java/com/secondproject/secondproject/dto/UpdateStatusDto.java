package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.Student_status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 학생 학적 상태 조회/수정용 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDto {
    private Student_status studentStatus; // 재학, 휴학 등
    private LocalDate leaveDate;           // leave_date
    private LocalDate retentionDate;       // retention_date
    private LocalDate returnDate;          // return_date
    private LocalDate graduationDate;      // graduation_date

}
