package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 학적변경신청 목록(요약) 전용 DTO.
 * - StatusChangeRequestDto에서 목록 테이블에 필요한 필드만 추출
 * - 민감/대용량 필드(content, 첨부 메타 등) 제외
 */
@Getter @Setter
public class StatusChangeListDto {

    private Long recordId;                 // 신청 식별자 (StudentRecord.id)
    private Student_status studentStatus;  // 신청 종류 코드
    private String title;                  // 신청 제목
    private LocalDate appliedDate;         // 신청일

    private Status status;                 // 진행 상태(PENDING/APPROVED/REJECTED)

    // 선택: 목록에 처리일을 노출하고 싶을 때 사용 (불필요하면 제거)
    private LocalDate processedDate;       // 처리일(없으면 null)

    // 기본 생성자 (Jackson/JPA 직렬화를 위해)
    public StatusChangeListDto() { }

    // 목록 기본 필드 생성자
    public StatusChangeListDto(Long recordId,
                                  Student_status studentStatus,
                                  String title,
                                  LocalDate appliedDate,
                                  Status status) {
        this.recordId = recordId;
        this.studentStatus = studentStatus;
        this.title = title;
        this.appliedDate = appliedDate;
        this.status = status;
    }

    // 처리일까지 포함하는 생성자(선택)
    public StatusChangeListDto(Long recordId,
                                  Student_status studentStatus,
                                  String title,
                                  LocalDate appliedDate,
                                  Status status,
                                  LocalDate processedDate) {
        this(recordId, studentStatus, title, appliedDate, status);
        this.processedDate = processedDate;
    }

    public Long getRecordId() { return recordId; }
    public Student_status getStudentStatus() { return studentStatus; }
    public String getTitle() { return title; }
    public LocalDate getAppliedDate() { return appliedDate; }
    public Status getStatus() { return status; }
    public LocalDate getProcessedDate() { return processedDate; }
}
