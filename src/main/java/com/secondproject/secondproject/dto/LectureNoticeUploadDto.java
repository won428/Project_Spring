package com.secondproject.secondproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LectureNoticeUploadDto {

    private String email;
    private Long id;
    private String title;
    private String content;

}
