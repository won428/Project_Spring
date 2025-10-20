package com.secondproject.secondproject.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.groups.Default;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "assignment")
public class Assignment {
    // 교수 과제 공지 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id", columnDefinition = "BIGINT")
    private long assignmentId; // 과제ID(PK)

    @Column(name = "ass_title", nullable = false, length = 200)
    private String title; // 공지 제목

    @Column(name = "ass_content", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content; // 공지 본문

    @Column(name = "is_enabled", nullable = false)
    @ColumnDefault("1")
    private boolean is_enabled = true;
    // 제출창구(제출 마감시 비활성화(0), 제출 진행중일시 활성화(1))

    @Column(name = "open_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openAt; // 제출 시작일

    @Column(name = "due_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueAt; // 제출 마감일

    @Column(name = "max_files", nullable = false)
    @ColumnDefault("3")
    private int max_files; // 업로드 가능한 파일 최대 갯수

    @Column(name = "max_size_mb", nullable = false)
    @ColumnDefault("50")
    private int max_size_mb = 50; // 파일 최대 용량(mb), 기본값 50

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 공지한사람(=교수이름)

    @Column(name = "create_at", columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt; // 공지 등록일

    @Column(name = "update_at", columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt; // 공지 수정일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture; // Lecture(강의) 테이블 FK참조
}
