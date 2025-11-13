package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.AttendStudent;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.AppealType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceAppealDto extends CreditAppealDto {

    private LocalDate attendanceDate;     // 강의 일자
    private AttendStudent attendStudent;  // BE_LATE, ABSENT 등
    private String attendanceDetail;      // 선택 사유(프론트 세부 내용)

    // JPQL 결과 매핑용 생성자
    public AttendanceAppealDto(
            Long appealId,
            Long lectureId,
            Long sendingId,
            Long receiverId,
            String title,
            String content,
            LocalDate appealDate,
            Status status,
            AppealType appealType,
            LocalDate attendanceDate,
            String attendanceStatus,
            String attendanceDetail
    ) {
        super.setAppealId(appealId);
        super.setLectureId(lectureId);
        super.setSendingId(sendingId);
        super.setReceiverId(receiverId);
        super.setTitle(title);
        super.setContent(content);
        super.setAppealDate(appealDate);
        super.setStatus(status);
        super.setAppealType(appealType);

        this.attendanceDate = attendanceDate;
        this.attendStudent = attendanceStatus != null
                ? AttendStudent.valueOf(attendanceStatus)
                : null;
        this.attendanceDetail = attendanceDetail;
    }
}
