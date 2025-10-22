package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class StatusChangeRequestDto {
    private Long recordId;           // 구분자(PK)
    private User user;            // 신청자(ID), StatusRecords의 applierId와 일치
    private Long statusId;           // 학적 확인용 번호(FK)
    private String title;            // 제목
    private String content;          // 내용(상세)
    private LocalDate appliedDate;   // 신청일
    private LocalDate processedDate; // 처리일
    private Status processStatus;    // 처리상태(enum)
    private Student_status academicRequest; // 변경신청학적(enum)
    private Long attachmentId;       // 첨부파일 식별자

    // StatusRecords 주요 필드(변동된 학적이 처리될 필드)
    private Student_status student_status;
    private LocalDate admissionDate;
    private LocalDate leaveDate;
    private LocalDate returnDate;
    private LocalDate graduationDate;
    private LocalDate retentionDate;
    private LocalDate expelledDate;
    private int totalCredit;
    private double currentCredit;
    private String studentImage;

}