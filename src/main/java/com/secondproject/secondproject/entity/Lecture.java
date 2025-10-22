package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "lecture")
public class Lecture {
    // 강의 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id; // 강의코드

    @Column(name = "lec_name", nullable = false)
    private String lecName; // 강의명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_lecture_user"))
    private User user; // 유저(교수)아이디가 들어갑니다

    @Column(nullable = false)
    private int credit; // 몇 학점인지 표기합니다.(2학점, 3학점)

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "lec_startDate")
    private LocalDate lecStartDate; // 강의 시작일

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "lec_endDate")
    private LocalDate lecEndDate; // 강의 종강일

    @Column(name = "lec_description")
    private String lecDescription; // 강의를 등록할때 작성하는 강의 한 줄 소개 입니다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", foreignKey = @ForeignKey(name = "fk_lecture_major"))
    private Major major; // 어느 학과 강의인지 표기합니다.

    @Column(name = "total_student", nullable = false)
    private int totalStudent; // 정원이 몇명인지 표기합니다. 정원을 초과하여 유저(학생이) 강의를 수강 할 수 없습니다.

    //강의 상태 : 대기중, 개설, 폐강등 ENUM에 필요한 상수 추가해주세요.
    // 요구사항 명세서에 지금 강의 등록 기능을 교수, 관리자 두명이 가지고 있는데
    // 교수가 강의등록을 신청했을때 관리자가 승인하면 상태가 개설로 바뀌는 방식이고,
    // 관리자가 강의를 직접 등록 할 수도 있게 기능 구상을 해놨습니다.
    @Enumerated(EnumType.STRING)
    private Status status;


}
