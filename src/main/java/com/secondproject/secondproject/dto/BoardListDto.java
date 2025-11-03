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
public class BoardListDto {
    Long id;
    String username;
    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
//    List<MultipartFile> files;


    public static BoardListDto fromEntity(Notice notice) {
        BoardListDto dto = new BoardListDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());

        if (notice.getUser() != null) {
            dto.setUsername(notice.getUser().getName());
            System.out.println(notice.getUser().getName());

        }

        dto.setCreatedAt(notice.getCreatedAt());
        dto.setUpdatedAt(notice.getUpdatedAt());
        return dto;
    }


}
