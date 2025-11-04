package com.secondproject.secondproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter @Setter
@Table(name = "grading_weights")
@Entity
public class GradingWeights {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gw_id")
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",foreignKey = @ForeignKey(name = "fk_grading_weights_lecture"))
    private Lecture lecture;

    @Column(name = "attendance_score",precision = 4, scale = 2,nullable = false)
    @Digits(integer = 2, fraction = 2)
    @ColumnDefault("20.00")
    BigDecimal attendanceScore = new BigDecimal("20.00");

    @Column(name = "assignment_score",precision = 4, scale = 2,nullable = false)
    @Digits(integer = 2, fraction = 2)
    @ColumnDefault("20.00")
    BigDecimal assignmentScore = new BigDecimal("20.00");

    @Column(name = "midterm_exam" ,precision = 4, scale = 2,nullable = false)
    @Digits(integer = 2, fraction = 2)
    @ColumnDefault("30.00")
    BigDecimal midtermExam = new BigDecimal("30.00");

    @Column(name = "final_exam",precision = 4, scale = 2,nullable = false)
    @Digits(integer = 2, fraction = 2)
    @ColumnDefault("30.00")
    BigDecimal finalExam = new BigDecimal("30.00");


}
