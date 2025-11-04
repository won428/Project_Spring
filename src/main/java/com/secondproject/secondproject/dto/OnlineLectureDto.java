package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.entity.Lecture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OnlineLectureDto {
    private String email;

    private Long lectureId;

    private String title; // 강의 이름

    private boolean disable = false; // 강의 활성화 유무, default false로 잡아놨는데 수정해야하면 수정해주세요
    
    private LocalDate startDate; // 강의 등록일

    private LocalDate endDate;

    private Long id; // 온라인 강의 코드
}
