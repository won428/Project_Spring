package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.LectureNotice;
import com.secondproject.secondproject.entity.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardResponseDto {
    Long id;
    String username;
    Long userid;
    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<AttachmentDto> attachmentDto;

    public static BoardResponseDto fromEntity(
            Notice notice,
            List<Attachment> attachments
    ) {
        BoardResponseDto dto = new BoardResponseDto();
        dto.setId(notice.getId());
        dto.setUserid(notice.getUser().getId());
        dto.setUsername(notice.getUser().getName());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setCreatedAt(notice.getCreatedAt());
        dto.setUpdatedAt(notice.getUpdatedAt());

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
