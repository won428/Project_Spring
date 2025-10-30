package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Student_status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
@Table(name = "status_record")
public class StatusRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long id;                    // 상태/학적번호(PK)

    @Enumerated(EnumType.STRING)
    @Column(name = "student_status", nullable=false)
    private Student_status studentStatus = Student_status.ENROLLED; // 학적 상태(재학, 휴학, 복학, 퇴학, 졸업), 기본값 재학

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stausRecord_user")) // 학적 유저ID, FK
    private User user;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate; // 입학일

    @Column(name = "leave_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaveDate; // 휴학일, NULL 허용

    @Column(name = "return_date")
    @JsonFormat(pattern = "yyyy-MM-dd")// 복학일, NULL 허용
    private LocalDate returnDate;

    @Column(name = "graduation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")// 졸업일, NULL 허용
    private LocalDate graduationDate;

    @Column(name = "retention_date")
    @JsonFormat(pattern = "yyyy-MM-dd")// 유급일, NULL 허용
    private LocalDate retentionDate;

    @Column(name = "expelled_date")
    @JsonFormat(pattern = "yyyy-MM-dd")// 퇴학일, NULL 허용
    private LocalDate expelledDate;
    // 제적일

    @Column(name = "level")
    @ColumnDefault("1")
    private int level = 1; // 학년

    @Column(name = "major_credit", nullable = false)
    private int  majorCredit = 0;

    @Column(name = "general_credit", nullable = false)
    private int  generalCredit = 0;

    @Column(name = "total_credit", nullable = false)
    private int  totalCredit = 0;               // 총 학점 (number)

    @Column(name = "current_credit", nullable = false)
    private double currentCredit = 0;              // 학기별 설정 (String, default "*")

    @Column(name = "student_image")
    private String studentImage;              // 증명사진 (이미지 저장경로 등)

    // 생성자, toString 등 추가 필요시 Lombok @NoArgsConstructor 사용 가능
}