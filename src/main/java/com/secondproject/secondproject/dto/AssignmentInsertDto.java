package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentInsertDto {
    private String email;

    private String title; // 공지 제목

    private String content; // 공지 본문

    private LocalDate openAt; // 제출 시작일

    private LocalDate dueAt; // 제출 마감일

    private int maxFiles; // 업로드 가능한 파일 최대 갯수

    private int maxSizeMb = 50; // 파일 최대 용량(mb), 기본값 50

    private User user; // 공지한사람(=교수이름)

    private Lecture lecture; // Lecture(강의) 테이블 FK참조

    private Long lectureId; // Lecture(강의) Id


//    public static AssignmentDto fromEntity(Assignment assignment) {
//
//
//    }

}
