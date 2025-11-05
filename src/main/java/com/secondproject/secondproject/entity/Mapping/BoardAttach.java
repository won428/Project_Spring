package com.secondproject.secondproject.entity.Mapping;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.LectureNotice;
import com.secondproject.secondproject.entity.Notice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MappingNotice")
public class BoardAttach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MapNotice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Notice_id")
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;


}
