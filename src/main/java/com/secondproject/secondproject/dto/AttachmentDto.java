package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.LectureNotice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentDto {
    private Long id;
    private String name;
    private String storedKey;
    private String contentType;
    private Long sizeBytes;
    private LocalDate uploadAt;
    private String sha256;


    public static AttachmentDto fromEntity(Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        AttachmentDto dto = new AttachmentDto();
        dto.setId(attachment.getId());
        dto.setName(attachment.getName());
        dto.setStoredKey(attachment.getStoredKey());
        dto.setContentType(attachment.getContentType());
        dto.setSizeBytes(attachment.getSizeBytes());
        dto.setUploadAt(attachment.getUploadAt());
        dto.setSha256(attachment.getSha256());

        return dto;
    }
}
