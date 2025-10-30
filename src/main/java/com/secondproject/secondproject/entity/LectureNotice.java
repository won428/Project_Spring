package com.secondproject.secondproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lecNotice")
public class LectureNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecNotice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "ln_title")
    private String lnTitle;

    @Column(name = "ln_content")
    private String lnContent;

    @Column(name = "ln_createAt")
    private LocalDateTime lnCreateAt;

    @Column(name = "ln_updateAt")
    private LocalDateTime lnUpdateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ol_id")
    private OnlineLecture onlineLecture;


    @PrePersist
        // @PrePersist: 엔티티가 처음 INSERT 되기 직전에 자동 실행
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (lnCreateAt == null) lnCreateAt = now; // 최초 생성 시간
        lnUpdateAt = now;                              // 최초 업데이트 시간도 now
    }

    @PreUpdate
        // @PreUpdate: 엔티티가 UPDATE되기 직전에 자동 실행
    void onUpdate() {
        lnUpdateAt = LocalDateTime.now();             // 수정 시마다 갱신
    }


}
