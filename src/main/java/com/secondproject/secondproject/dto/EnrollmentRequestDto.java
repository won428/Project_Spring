package com.secondproject.secondproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentRequestDto {
    private Long userId; // 유저정보
    private Long lectureId; // 강의정보
    private Long gradeId; // 성적정보
    private String completionDiv; // 이수구분
    private String status; // 수강상태(수강중, 종강 등)
}
