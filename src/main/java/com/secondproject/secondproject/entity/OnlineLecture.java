package com.secondproject.secondproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "onlineLecture")
public class OnlineLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ol_id")
    private Long id; // 온라인 강의 코드

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false, foreignKey = @ForeignKey(name = "fk_onlineLecture_lecture"))
    private Lecture lecture;

    @Column(name = "ol_path", nullable = false)
    private String path; // 온라인 로컬주소 << 매핑관련 확인해주세요

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ol_date")
    private LocalDate updatedDate; // 강의 등록일

    @Column(name = "ol_disable", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean disable = false; // 강의 활성화 유무, default false로 잡아놨는데 수정해야하면 수정해주세요

    @Column(name = "ol_name", nullable = false)
    private String name; // 강의 이름


}
