package com.secondproject.secondproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter @Setter
@Table(name = "grading_weights")
public class GradingWeights {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gw_id")
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",foreignKey = @ForeignKey(name = "fk_grading_weights_lecture"))
    private Lecture lecture;

    @Column(name = "midterm_exam" ,nullable = false)
    @ColumnDefault("30.00")
    Double midtermExam = 30.00;

    @Column(name = "final_exam", nullable = false )
    @ColumnDefault("30.00")
    Double finalExam = 30.00;

    @Column(name = "attendance_score", nullable = false)
    @ColumnDefault("20.00")
    Double attendanceScore = 20.00;

    @Column(name = "assignment_score", nullable = false)
    @ColumnDefault("20.00")
    Double assignmentScore = 20.00;
}
