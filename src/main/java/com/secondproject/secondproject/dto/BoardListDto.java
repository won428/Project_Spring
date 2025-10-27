package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Notice;
import com.secondproject.secondproject.entity.User;
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


    public static BoardListDto fromEntity(Notice notice) {
        BoardListDto dto = new BoardListDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());

        // User 엔티티에서 필요한 작성자 이름만 꺼내서 DTO에 담습니다.
        if (notice.getUser() != null) {
            dto.setUsername(notice.getUser().getName());
            System.out.println(notice.getUser().getName());

        }

        dto.setCreatedAt(notice.getCreatedAt());
        dto.setUpdatedAt(notice.getUpdatedAt());
        return dto;
    }


}
