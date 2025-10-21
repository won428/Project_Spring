package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Student_status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
    @Column(nullable=false)
    private Student_status student_status; // 학적 상태(재학, 휴학, 복학, 퇴학, 졸업)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stausRecord_user")) // 학적 유저ID, FK
    private User user;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate admissionDate; // 입학일

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaveDate; // 휴학일, NULL 허용

    @JsonFormat(pattern = "yyyy-MM-dd")// 복학일, NULL 허용
    private LocalDate returnDate;

    @JsonFormat(pattern = "yyyy-MM-dd")// 졸업일, NULL 허용
    private LocalDate graduation_date;

    @JsonFormat(pattern = "yyyy-MM-dd")// 유급일, NULL 허용
    private LocalDate retention_date;

    @JsonFormat(pattern = "yyyy-MM-dd")// 퇴학일, NULL 허용
    private LocalDate expelled_date;
    // 제적일

    @Column(nullable = false)
    private int  totalCredit = 0;               // 총 학점 (number)

    @Column(nullable = false)
    private double currentCredit = 0;              // 학기별 설정 (String, default "*")

    private String student_image;              // 증명사진 (이미지 저장경로 등)

    // 생성자, toString 등 추가 필요시 Lombok @NoArgsConstructor 사용 가능
}