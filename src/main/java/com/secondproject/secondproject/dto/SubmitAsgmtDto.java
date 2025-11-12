package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.SubmitStatus;
import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.SubmitAsgmt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubmitAsgmtDto {
    private Long id; // 과제ID(PK)

    private String username;

    private String title; // 공지 제목

    private String content; // 공지 본문

    private Long assignmentId;//과제 Id

    private SubmitStatus submitStatus = SubmitStatus.PENDING; // 제출 시작일

    private LocalDateTime submitAt; // 과제 제출일

    private LocalDateTime updateAt; // 과제 수정일

    private List<AttachmentDto> attachmentSubmittedDto;

    public static SubmitAsgmtDto fromEntity(SubmitAsgmt submission, List<Attachment> attachments) {
        SubmitAsgmtDto dto = new SubmitAsgmtDto();
        dto.setId(submission.getId());
        dto.setUsername(submission.getUser().getName());
        dto.setTitle(submission.getTitle());
        dto.setContent(submission.getContent());
        dto.setSubmitStatus(submission.getSubmitStatus());
        dto.setSubmitAt(submission.getSubmitAt());
        dto.setUpdateAt(submission.getUpdateAt());

        // '이 제출물'에 속한 파일 목록을 DTO로 변환하여 설정
        if (attachments != null && !attachments.isEmpty()) {
            dto.attachmentSubmittedDto = attachments.stream()
                    .map(AttachmentDto::fromEntity) // (가정) AttachmentDto에 fromEntity
                    .toList();
        } else {
            dto.attachmentSubmittedDto = new ArrayList<>();
        }

        return dto;
    }

}
