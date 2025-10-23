package com.secondproject.secondproject.entity;

import com.secondproject.secondproject.Enum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@Entity
@Table(name = "course_registration")
public class CourseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_courseRegister_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",foreignKey = @ForeignKey(name = "fk_courseRegister_lecture"))
    private Lecture lecture;

    @Column(nullable = false)
    private LocalDateTime date; // 신청날짜 ~년도 ~월 ~일 ~몇분 ~몇초

    @Column(nullable = false)
    private Status status; // 신청 상태

}