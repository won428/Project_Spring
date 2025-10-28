package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.LectureNotice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LectureNoticeListWithFileDto {
    Long id;
    String username;
    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<Attachment> files;


    public static LectureNoticeListWithFileDto fromEntity(
            LectureNotice lectureNotice,
            List<Attachment> attachments
    ) {
        LectureNoticeListWithFileDto dto = new LectureNoticeListWithFileDto();
        dto.setId(lectureNotice.getId());
        dto.setUsername(lectureNotice.getUser().getName());
        dto.setTitle(lectureNotice.getLnTitle());
        dto.setContent(lectureNotice.getLnContent());
        dto.setCreatedAt(lectureNotice.getLnCreateAt());
        dto.setUpdatedAt(lectureNotice.getLnUpdateAt());
        List<Attachment> ListAttach = new ArrayList<>();
        if (!attachments.isEmpty()) {
            for (Attachment attachment : attachments) {
                ListAttach.add(attachment);
            }
            dto.setFiles(ListAttach);
        }
        return dto;
    }
}
