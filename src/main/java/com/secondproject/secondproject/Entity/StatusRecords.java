package com.secondproject.secondproject.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Student_status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "statusRecords")
public class StatusRecords {
    // 학적 상세정보 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "status_id")
    private Long id; // 학적 코드

    @Column(nullable=false)
    private Student_status student_status; // 학적 상태(재학, 휴학, 복학, 퇴학, 졸업)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate admissionDate; // 입학일

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate 	leaveDate; // 휴학일

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate 	returnDate; // 복학일

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate 	graduation_date; //졸업일

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate 	retention_date; // 유급일

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate 	expelled_date; // 퇴학일

    @Column(nullable = false)
    private int totalCredit = 0; // 총 학점

    @Column(nullable = false)
    private double currentCredit = 0; // 당학기 성적

    private String student_image; // 증명사진
}
