package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Lecture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureBasicInfoDto {
    private Long lectureId;
    private String lecName;   // 강의명
    private Long userId;      // 교수 id
    private String userName;  // 교수 이름

}