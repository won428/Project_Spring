package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.AttendStudent;
import com.secondproject.secondproject.Enum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "UserLectureProgress")
public class UserLectureProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ol_progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ol_id", foreignKey = @ForeignKey(name = "fk_ol_progress_lec"))
    private OnlineLecture onlineLecture;//온라인 강의

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_ol_progress_user"))
    private User user; //학생

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_status")
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_at_status")
    private AttendStudent attendanceStatus = AttendStudent.ABSENT;

    @Column(name = "progress_last_viewed_sec")
    private int lastViewedSec = 0;

    @Column(name = "progress_total_watched_sec")
    private int totalWatchedSec = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "progress_updated_at")
    private LocalDateTime updatedAt;
}
