package com.secondproject.secondproject.Entity;

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
    // 파일 저장 경로 테이블, 파일을 업로드하는 기능을 쓰는 경우 매핑 엔터티(테이블) 한개 만드시고
    // 기능 - 매핑테이블 - 파일저장 경로 테이블로 매핑 하시면됩니다.
    // 매핑하는 이유는 파일이 두개 이상 업로드 될 수있어서 이렇게 설계 했습니다.
    private Long attachment_id; // 저장 경로 PK

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attachment_id")
    private Long id; // 저장 경로 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
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
