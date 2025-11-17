package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.LectureNotice;
import com.secondproject.secondproject.entity.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LectureNoticeListDto {
    Long id;
    String username;
    String title;
    String content;
    Long LectureId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
//    List<MultipartFile> files;


    public static LectureNoticeListDto fromEntity(LectureNotice notice) {
        LectureNoticeListDto dto = new LectureNoticeListDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getLnTitle());
        dto.setContent(notice.getLnContent());
        dto.setLectureId(notice.getLecture().getId());
        if (notice.getUser() != null) {
            dto.setUsername(notice.getUser().getName());
            System.out.println(notice.getUser().getName());

        }

        dto.setCreatedAt(notice.getLnCreateAt());
        dto.setUpdatedAt(notice.getLnUpdateAt());
        return dto;
    }


}
