package com.secondproject.secondproject.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    private String original_name; // 원본 이름

    private String stored_key;  // 변환된 파일명(로컬주소)

    private String content_type; // 기능 타입

    private Long size_bytes; // 파일 용량

    private LocalDate upload_at; // 업로드일



}
