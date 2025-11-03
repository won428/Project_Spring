package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.SubmitStatus;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.SubmitAsgmt;
import com.secondproject.secondproject.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AssignSubmitInsertDto {
    private String email; //user 호출용 email

    private Long lectureId; //강의 아이디

    private Long assignId;// 과제 아이디

    private String title; // 공지 제목

    private String content; // 공지 본문

//    private User user; // user 주입용 제출자
//
//    private Lecture lecture; // Lecture(강의) 테이블 FK참조


}
