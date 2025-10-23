package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "attendance_records")
public class Attendance_records {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id; // 출결 기록 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendanceRecords_user"))
    private User user; // 유저id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enroll_Id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendanceRecords_enroll"))
    private com.secondproject.secondproject.entity.Enrollment enrollment; // 수강id

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate attendance_date; //출결일

    @Column(nullable = false)
    private String attendance_status; // 츨결상태
    // 일단 String인데 ENUM으로 관리하실거면 수정 필요합니다.
}