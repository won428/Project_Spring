package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.BoardType;
import com.secondproject.secondproject.Enum.InquiryStatus;
import com.secondproject.secondproject.Enum.Tag;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDto {

    Long user;

    String userName;

    Long postNumber;

    String title;

    String content;

    Boolean isPrivate;

    @Enumerated(EnumType.STRING)
    BoardType board;

    @Enumerated(EnumType.STRING)
    Tag tag;

    @Enumerated(EnumType.STRING)
    InquiryStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updateAt;

    int viewCount;

    List<AttachmentDto> attachmentDtos;
}
