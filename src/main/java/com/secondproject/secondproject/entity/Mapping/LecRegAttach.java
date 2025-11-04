package com.secondproject.secondproject.entity.Mapping;


import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.LectureNotice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mappingLectureReg")
public class LecRegAttach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecRegAttach_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;
}
