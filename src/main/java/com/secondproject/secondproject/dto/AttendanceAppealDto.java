package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.AppealType;
import com.secondproject.secondproject.Enum.AttendStudent;
import com.secondproject.secondproject.Enum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceAppealDto extends CreditAppealDto {

    private AttendStudent attendStudent;   // BE_LATE, ABSENT 등
    private String attendanceDetail; // 프론트 세부선택지 저장용
    private LocalDate lectureDate;

}
