package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.StudentRecord;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import com.secondproject.secondproject.entity.User;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class StatusChangeRequestDto {

    // StudentRecord 전체 매핑(요청/응답 공용)
    private Long recordId;                // StudentRecord.id (응답 시 세팅, 생성 요청 시 null 권장)


    private Long userId;                  // StudentRecord.user.id (요청 시 필수)
    private Student_status studentStatus; // StudentRecord.studentStatus

    private String title;                 // StudentRecord.title (record_title)
    private String content;               // StudentRecord.content (record_content)

    private LocalDate appliedDate;        // StudentRecord.appliedDate (요청 시 today, 서버 보정 가능)
    private LocalDate processedDate;      // StudentRecord.processedDate (응답/승인 시 세팅)

    private LocalDate startDate;   // StudentRecord.startDate
    private LocalDate endDate;     // StudentRecord.endDate

    private Status status;                // StudentRecord.status (생성 시 PENDING 강제 권장)

    // Attachment 참조(FK)
    private Long attachmentId;            // 첨부 PK(없으면 null), 파일 저장 경로
    private User user;
    private String name;
    private String storedKey;
    private String contentType;
    private Long sizeBytes;
    private LocalDate uploadAt;
    private String sha256; // 해시검증용 입니다.

    public StatusChangeRequestDto(StudentRecord studentRecord, Attachment attachment) {
        this. recordId = studentRecord.getId();
        this.userId = studentRecord.getUser().getId();
        this.studentStatus = studentRecord.getStudentStatus();
        this.title = studentRecord.getTitle();
        this.content = studentRecord.getContent();
        this.appliedDate = studentRecord.getAppliedDate();
        this.processedDate = studentRecord.getProcessedDate();
        this.status = studentRecord.getStatus();
        this.startDate = studentRecord.getStartDate();
        this.endDate = studentRecord.getEndDate();

        if(attachment != null) {
            this.attachmentId = attachment.getId();
            this.user = attachment.getUser();
            this.name = attachment.getName();
            this.storedKey = attachment.getStoredKey();
            this.contentType = attachment.getContentType();
            this.sizeBytes = attachment.getSizeBytes();
            this.uploadAt = attachment.getUploadAt();
            this.sha256 = attachment.getSha256();

        }
    }

    public StatusChangeRequestDto(Long recordId, Long userId, Student_status studentStatus, String title, String content, LocalDate appliedDate, LocalDate processedDate, Status status, Long attachmentId) {
        this.recordId = recordId;
        this.userId = userId;
        this.studentStatus = studentStatus;
        this.title = title;
        this.content = content;
        this.appliedDate = appliedDate;
        this.processedDate = processedDate;
        this.status = status;
        this.attachmentId = attachmentId;
    }
}
