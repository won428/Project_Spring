package com.secondproject.secondproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LectureNoticeUploadDto {

    private String username;
    private Long id;
    private String title;
    private String content;
//    private List<String> existingFileKeys;

}
