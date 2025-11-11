package com.secondproject.secondproject.entity;

import com.secondproject.secondproject.Enum.CompletionDiv;
import com.secondproject.secondproject.Enum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "enrollment")
public class Enrollment {
    // 수강 엔터티 입니다.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enroll_Id")
    private Long id; // 수강id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_user"))
    private User user; // 수강중인 유저 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", foreignKey = @ForeignKey(name = "fk_enrollment_lecture"))
    private Lecture lecture; // 수강중인 강의 코드

    @OneToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade; // 성적 정보 id

//    @Enumerated(EnumType.STRING)
//    @Column(name = "completion_div", nullable = false)
//    private CompletionDiv completionDiv;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // 수강 상태(수강중인지, 종강인지), 일단 Status Enum클래스로 한번에 관리하려고 하는데 분리 필요하면 분리하셔도 될 것 같습니다.

}