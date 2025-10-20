package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class StatusChangeRequestDto {
    private Long recordId;           // 구분자(PK)
    private Long applier;            // 신청자(ID), StatusRecords의 applierId와 일치
    private Long statusId;           // 상태기록(FK)
    private String title;            // 제목
    private String content;          // 내용(상세)
    private LocalDate appliedDate;   // 신청일
    private LocalDate processedDate; // 처리일
    private Status processStatus;    // 처리상태(enum)
    private Student_status academicRequest; // 변경신청학적(enum)
    private Long attachmentId;       // 첨부파일 식별자

    private LocalDate leaveDate;
    private LocalDate returnDate;
    private LocalDate graduationDate;
    private LocalDate retentionDate;
    private LocalDate expelledDate;
    private int totalCredit;
    private double currentCredit;
    private String studentImage;

    // StatusRecords 기반 생성자 - 엔티티 필드명에 맞춰 매핑
    public StatusChangeRequestDto(StatusRecords record) {
        this.recordId = record.getId();                 // status_id
        this.applier = record.getApplierId();           // applierId
        this.statusId = record.getId();                  // 없으면 recordId 재사용 가능
        // 아래가 StatusRecords에 없으면 null 허용하거나 별도 처리 필요
        this.title = null;                                // title 필드가 없으면 null 처리
        this.content = null;                              // content 필드가 없으면 null 처리
        this.appliedDate = record.getAdmissionDate();    // admissionDate (입학일)
        this.processedDate = null;                        // processedDate 엔티티에 없으면 별도 처리
        this.processStatus = null;                        // processStatus 엔티티에 없으면 null
        this.academicRequest = record.getStudent_status();// 학적 상태
        this.attachmentId = null;                         // attachmentId 엔티티에 없으면 null
    }
}
