package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.SubmitStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "submit_asgmnt")
public class SubmitAsgmt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submit_id", columnDefinition = "BIGINT")
    private Long id; // 과제제출ID(PK)

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // 교수공지 삭제기능 대신 비활성화 기능
    @JoinColumn(name = "assignment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_submit_asgmnt"))
    private Assignment assignment; //교수공지ID(FK)

    @ManyToOne(fetch = FetchType.LAZY) // 제출자(학생)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_submit_user"))
    private User user;

    @Column(name = "submit_title", length = 200, nullable = false)
    private String title; // 제출 폼 제목

    @Column(name = "submit_content", nullable = true, columnDefinition = "MEDIUMTEXT")
    private String content; // 제출 폼 본문

    @Enumerated(EnumType.STRING)
    @Column(name = "submit_status", nullable = false, columnDefinition = "ENUM('COMPLETED','PENDING') DEFAULT 'PENDING'")
    private SubmitStatus submitStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "submit_at", columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime submitAt; // 제출일

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_at", columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateAt; // 제출 수정일


    @PrePersist
        // @PrePersist: 엔티티가 처음 INSERT 되기 직전에 자동 실행
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (submitAt == null) submitAt = now; // 최초 생성 시간
        updateAt = now;                              // 최초 업데이트 시간도 now
    }

    @PreUpdate
        // @PreUpdate: 엔티티가 UPDATE되기 직전에 자동 실행
    void onUpdate() {
        updateAt = LocalDateTime.now();

    }
}