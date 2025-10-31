package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.LectureNotice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NoticeResponseDto {
    Long id;
    String username;
    Long userid;
    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<AttachmentDto> attachmentDto;

    public static NoticeResponseDto fromEntity(
            LectureNotice lectureNotice,
            List<Attachment> attachments
    ) {
        NoticeResponseDto dto = new NoticeResponseDto();
        dto.setId(lectureNotice.getId());
        dto.setUserid(lectureNotice.getUser().getId());
        dto.setUsername(lectureNotice.getUser().getName());
        dto.setTitle(lectureNotice.getLnTitle());
        dto.setContent(lectureNotice.getLnContent());
        dto.setCreatedAt(lectureNotice.getLnCreateAt());
        dto.setUpdatedAt(lectureNotice.getLnUpdateAt());

        List<AttachmentDto> ListAttach = new ArrayList<>();
        if (!attachments.isEmpty() || attachments != null) {
            for (Attachment attachment : attachments) {
                AttachmentDto Ado = new AttachmentDto();
                Ado.setId(attachment.getId());
                Ado.setName(attachment.getName());
                Ado.setId(attachment.getUser().getId());
                Ado.setContentType(attachment.getContentType());
                Ado.setSha256(attachment.getSha256());
                Ado.setSizeBytes(attachment.getSizeBytes());
                Ado.setStoredKey(attachment.getStoredKey());
                Ado.setUploadAt(attachment.getUploadAt());

                ListAttach.add(Ado);
            }

            dto.setAttachmentDto(ListAttach);
        }
        return dto;
    }

}
