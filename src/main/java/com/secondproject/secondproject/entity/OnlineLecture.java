package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "online_lecture")
public class OnlineLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ol_id")
    private Long id; // 온라인 강의 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_ol_lecture_user"))
    private User user; // 유저(교수)아이디가 들어갑니다

    @Column(name = "ol_username")
    private String uname; // 작성자 이름

    @Column(name = "ol_path", nullable = false)
    private String path; // 온라인 로컬주소 << 매핑관련 확인해주세요

    @Column(name = "ol_disable", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean disable = false; // 강의 활성화 유무, default false로 잡아놨는데 수정해야하면 수정해주세요

    @Column(name = "ol_name", nullable = false)
    private String title; // 강의 이름

    @Column(name = "ol_uploaded")
    private LocalDate createdAt;

    @Column(name = "ol_date")
    private LocalDateTime updatedDate; // 강의 등록일

    @Column(name = "ol_vidlength")
    private int vidLength = 0;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "ol_end")
    private LocalDateTime endDate;

    @Column(name = "lecture_atRate")
    private double atRate; // 온라인 출석률

    @PrePersist
        // @PrePersist: 엔티티가 처음 INSERT 되기 직전에 자동 실행
    void onCreate() {
        LocalDate now = LocalDate.now();
        if (createdAt == null) createdAt = now; // 최초 생성 시간
    }


}