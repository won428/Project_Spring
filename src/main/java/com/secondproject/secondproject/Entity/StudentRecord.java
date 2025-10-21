package com.secondproject.secondproject.Entity;

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
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "student_record")
public class StudentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long record_id;                  // 유저ID(FK), number

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_studentRecord_user")) // 신청자ID
    private User user;

    @Enumerated(EnumType.STRING)
    private Status statusRecords;                // 상태/학적번호(FK), number

    @Column(nullable = false)
    private String title;                 // 제목(varchar)

    @Column(nullable = false)
    private String content;               // 내용(varchar)

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applied_date;        // 신청일(date)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate processed_date;      // 처리일(date), null 허용

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;         // 처리상태

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Student_status studentStatus;     // 변경신청목적(enum, varchar)

    // 연관관계 매핑이 필요하면 @ManyToOne, @JoinColumn(userId) 등 추가
}