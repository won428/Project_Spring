package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.AppealType;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.Appeal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditAppealDto {

    private Long lectureId;       //
    private Long sendingId;       // 보내는 유저 ID (학생)
    private Long receiverId;      // 받는 유저 ID (교수)
    private String title;           // 이의제기 제목
    private String content;         // 이의제기 내용
    private LocalDate appealDate;   // 신청 날짜
    private Status status;          // 처리 상태
    private AppealType appealType;  // 이의제기 타입
}
