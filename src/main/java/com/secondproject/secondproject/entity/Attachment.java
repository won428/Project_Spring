package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.FileType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "attachment")
public class Attachment {
    // 로컬 파일들 전체 다 저장되는 엔터티 입니다.
    // 파일 다운로드를 할때 (parentType,parentId)형태로
    // private List<MultipartFile> files; @ModelAttribute 사용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id; // 저장 경로 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_attachment_user"))
    private User user;

    @Column(name = "original_name")
    private String name; // 원본 이름

    @Column(name = "stored_key", nullable = false)
    private String storedKey;  // 변환된 파일명(로컬주소)

    @Column(name = "content_type", nullable = false)
    private String contentType; // 파일 타입

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes; // 파일 용량

    @Column(name = "upload_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate uploadAt; // 업로드일

    private String sha256; // 해시검증용 입니다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeal_id") // FK 컬럼
    private Appeal appeal;


    @PrePersist
        // @PrePersist: 엔티티가 처음 INSERT 되기 직전에 자동 실행
    void onCreate() {
        LocalDate now = LocalDate.now();
        if (uploadAt == null) uploadAt = now; // 최초 생성 시간
        uploadAt = now;                              // 최초 업데이트 시간도 now
    }

}