package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.LectureNotice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LectureNoticeListWithFileDto {
    Long id;
    String username;
    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<Attachment> files;


    public static LectureNoticeListWithFileDto fromEntity(LectureNotice notice) {
        LectureNoticeListWithFileDto dto = new LectureNoticeListWithFileDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getLnTitle());
        dto.setContent(notice.getLnContent());

        if (notice.getUser() != null) {
            dto.setUsername(notice.getUser().getName());
            System.out.println(notice.getUser().getName());

        }


        dto.setCreatedAt(notice.getLnCreateAt());
        dto.setUpdatedAt(notice.getLnUpdateAt());
        return dto;
    }


}
