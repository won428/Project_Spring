package com.secondproject.secondproject.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Setter
@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notice_author_user"
    ))
    private User user;

    @Column(name = "notice_title", nullable = false, length = 200)
    private String noticeTitle;

    @Lob
    @Column(name = "notice_content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String noticeContent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "notice_create_at", nullable = false)
    private LocalDateTime noticeCreateAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "notice_update_at", nullable = false)
    private LocalDateTime noticeUpdateAt;


//    @PrePersist // @PrePersist: 엔티티가 처음 INSERT되기 직전에 자동 실행
//    void onCreate() {
//        LocalDateTime now = LocalDateTime.now();
//        if (noticeCreateAt == null) noticeCreateAt = now; // 최초 생성 시간
//        noticeUpdateAt = now;                              // 최초 업데이트 시간도 now
//    }
//
//    @PreUpdate // @PreUpdate: 엔티티가 UPDATE되기 직전에 자동 실행
//    void onUpdate() {
//        noticeUpdateAt = LocalDateTime.now();             // 수정 시마다 갱신
//    }
}
