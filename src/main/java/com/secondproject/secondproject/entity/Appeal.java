package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appeal_id")
    private Long id; // 이의제기 코드

    @Column(nullable = false)
    private String sendingId; // 보내는 유저 아이디 입니다.

    @Column(nullable = false)
    private String receiverId; // 받는 유저 아이디 입니다

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enroll_Id", nullable = false,foreignKey = @ForeignKey(name = "fk_appeal_enroll"))
    private com.secondproject.secondproject.entity.Enrollment enrollment;

    @Column(name = "appeal_title", nullable = false)
    private String title; // 이의제기 제목입니다.

    @Column(name = "appeal_content", nullable = false)
    private String content; // 이의제기 내용입니다.

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "appeal_date", nullable = false)
    private LocalDate appealDate; // 처리 신청 날짜 입니다. 처리 완료 날짜도 칼럼으로 넣을지는 고민이 필요합니다.

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // 이의제기의 처리상태를 의미합니다. 처리, 처리완료등

    @Column(name = "appeal_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppealType appealType; // 이의제기 타입은 이의제기가 출결, 과제, 성적등 어디에 이의제기를 하는지 정의합니다.
}
