package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import com.secondproject.secondproject.Enum.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class StatusChangeRequestDto {
    private Long recordId;        // 신청(PK)
    private Long userId;          // 신청자(ID, FK)
    private Long statusId;        // 상태기록(FK)
    private String title;         // 제목
    private String content;       // 내용(상세)
    private LocalDate appliedDate;    // 신청일
    private LocalDate processedDate;  // 처리일
    private Status processStatus;     // 처리상태(enum)
    private Student_status academicRequest; // 변경신청학적(enum)
    private UserType userType;        // 권한(학생만)
    private Long attachmentId;        // 첨부파일 식별자
}


