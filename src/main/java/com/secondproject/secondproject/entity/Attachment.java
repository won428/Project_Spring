package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "attachment")
public class Attachment {
    // 로컬 파일들 전체 다 저장되는 엔터티 입니다.
    // 진성님 한번씩 검토해서 수정할거 수정해주세요 !

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id; // 저장 경로 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_attachment_user"))
    private User user;

    @Column(name = "original_name")
    private String name; // 원본 이름

    @Column(nullable = false)
    private String stored_key;  // 변환된 파일명(로컬주소)

    @Column(nullable = false)
    private String content_type; // 파일 타입

    @Column(nullable = false)
    private Long size_bytes; // 파일 용량

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate upload_at; // 업로드일

    private String sha256; // 해시검증용 입니다.
}
