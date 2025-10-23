package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "student_record")
public class StudentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;                  // 유저ID(FK), number

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_studentRecord_user")) // 신청자ID
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name ="Student_status" )
    private Student_status studentStatus;                // 신청 목적

    @Column(name = "record_title", nullable = false)
    private String title;                 // 제목(varchar)

    @Column(name = "record_content", nullable = false)
    private String content;               // 내용(varchar)

    @Column(name = "applied_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appliedDate;        // 신청일(date)

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "processed_date")
    private LocalDate processedDate;      // 처리일(date), null 허용

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;         // 처리상태


    // 연관관계 매핑이 필요하면 @ManyToOne, @JoinColumn(userId) 등 추가
}