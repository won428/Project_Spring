package com.secondproject.secondproject.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.mapping.ToOne;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "lecture")
public class Lecture {
    // 강의 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "lecture_id")
    private Long id; // 강의코드

    @Column(nullable = false)
    private String lec_name; // 강의명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 유저(교수)아이디가 들어갑니다

    @Column(nullable = false)
    private int credit; // 몇 학점인지 표기합니다.(2학점, 3학점)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lec_startDate; // 강의 시작일

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lec_endDate; // 강의 종강일

    private String lec_description; // 강의를 등록할때 작성하는 강의 한 줄 소개 입니다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", nullable = false)
    private Major major; // 어느 학과 강의인지 표기합니다.

    @Column(nullable = false)
    private int total_student; // 정원이 몇명인지 표기합니다. 정원을 초과하여 유저(학생이) 강의를 수강 할 수 없습니다.

    //강의 상태 : 대기중, 개설, 폐강등 ENUM에 필요한 상수 추가해주세요.
    // 요구사항 명세서에 지금 강의 등록 기능을 교수, 관리자 두명이 가지고 있는데
    // 교수가 강의등록을 신청했을때 관리자가 승인하면 상태가 개설로 바뀌는 방식이고,
    // 관리자가 강의를 직접 등록 할 수도 있게 기능 구상을 해놨습니다.
    @Enumerated(EnumType.STRING)
    private Status status;

    // 수정필요
    private long ol_id; // 강의와 관련된 온라인 강의 정보를 표기합니다. << 강의당 온라인 강의 1개인게 아니면 매핑 필요할거 같아요





}
