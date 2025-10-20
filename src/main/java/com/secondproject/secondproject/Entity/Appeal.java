package com.secondproject.secondproject.Entity;

import com.secondproject.secondproject.Enum.AppealType;
import com.secondproject.secondproject.Enum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "appeal")
public class Appeal {
    // 이의제기 엔터티입니다.

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "appeal_id")
    private Long id; // 이의제기 코드

    @Column(nullable = false)
    private String sending_id; // 보내는 유저 아이디 입니다.

    @Column(nullable = false)
    private String receiver_id; // 받는 유저 아이디 입니다

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enroll_Id", nullable = false)
    private Enrollment enrollment;

    @Column(nullable = false)
    private String title; // 이의제기 제목입니다.

    @Column(nullable = false)
    private String content; // 이의제기 내용입니다.

    @Column(nullable = false)
    private LocalDate appeal_date; // 처리 신청 날짜 입니다. 처리 완료 날짜도 칼럼으로 넣을지는 고민이 필요합니다.

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // 이의제기의 처리상태를 의미합니다. 처리, 처리완료등

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppealType appealType; // 이의제기 타입은 이의제기가 출결, 과제, 성적등 어디에 이의제기를 하는지 정의합니다.
}
