package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.FileType;
import com.secondproject.secondproject.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardDto {
    private String email;

    private User user; // 작성자 ID

    private String username;

    private String title; // 공지사항 제목

    private String content; // 공지사항 본문

}
