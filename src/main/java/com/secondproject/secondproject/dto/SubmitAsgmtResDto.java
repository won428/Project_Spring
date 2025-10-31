package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.SubmitStatus;
import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.Attachment;
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
public class SubmitAsgmtResDto {
    private Long id; // 과제ID(PK)

    private String username; // 제출자 이름

    private String title; // 공지 제목

    private String content; // 공지 본문

    private Long assignmentId;//과제 Id

    private SubmitStatus submitStatus; // 제출 시작일

    private LocalDateTime submitAt; // 과제 제출일

    private LocalDateTime updateAt; // 과제 수정일

    private List<AttachmentDto> attachmentDto;

//    public static SubmitAsgmtResDto fromEntity(
//            Assignment assignment,
//            List<Attachment> attachments) {
//        SubmitAsgmtResDto dto = new SubmitAsgmtResDto();
//        dto.setId(assignment.getId());
//        dto.setTitle(assignment.getTitle());
//        dto.setContent(assignment.getContent());
//        dto.setOpenAt(assignment.getOpenAt());
//        dto.setDueAt(assignment.getDueAt());
//        if (assignment.getUser() != null) {
//            dto.setUsername(assignment.getUser().getName());
//        }
//        dto.setCreateAt(assignment.getCreateAt());
//        dto.setUpdateAt(assignment.getUpdateAt());
//
//
//        List<AttachmentDto> ListAttach = new ArrayList<>();
//        if (!attachments.isEmpty() || attachments != null) {
//            for (Attachment attachment : attachments) {
//                AttachmentDto Ado = new AttachmentDto();
//                Ado.setId(attachment.getId());
//                Ado.setName(attachment.getName());
//                Ado.setId(attachment.getUser().getId());
//                Ado.setContentType(attachment.getContentType());
//                Ado.setSha256(attachment.getSha256());
//                Ado.setSizeBytes(attachment.getSizeBytes());
//                Ado.setStoredKey(attachment.getStoredKey());
//                Ado.setUploadAt(attachment.getUploadAt());
//
//                ListAttach.add(Ado);
//            }
//
//            dto.setAttachmentDto(ListAttach);
//        }
//
//
//        return dto;
//    }


}
