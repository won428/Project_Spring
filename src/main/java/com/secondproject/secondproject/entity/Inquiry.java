package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.InquiryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Table(name = "inquiry")
@Entity
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private long inquiry_id; // 게시번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_inquiry_user"))
    private User user; // 작성자 ID

    @Column(name = "inquiry_title", nullable = false, length = 200)
    private String inquiryTitle; // 문의글 제목

    @Column(name = "inquiry_content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String inquiryContent; // 문의글 내용

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false; // 비공개 여부, 디폴트 = 공개

    @Column(name = "inquiry_created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inquiryCreatedAt; // 작성일(insert), 직전 자동으로 채워짐

    @Column(name = "inquiry_updated_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inquiryUpdatedAt; // 수정일(update) 직전 자동으로 채워짐

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_status", nullable = false)
    private InquiryStatus inquiryStatus = InquiryStatus.PENDING; // 처리상태(대기중,처리완료)

    // 문의글 등록일을 INSERT 직전에 자동으로 채우는 기능
    @PrePersist
    void prePersist() {
        var now = LocalDateTime.now();
        if (inquiryCreatedAt == null) inquiryCreatedAt = now; // 최초 생성일
        inquiryUpdatedAt = now;                                // 최초 수정일도 now
    }

    // 문의글 수정일을 UPDATE 직전에 자동으로 채우는 기능
    @PreUpdate
    void preUpdate() {
        inquiryUpdatedAt = LocalDateTime.now();                // 수정될 때마다 갱신
    }
}

