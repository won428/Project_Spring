package com.secondproject.secondproject.entity.Mapping;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.Inquiry;
import com.secondproject.secondproject.entity.Lecture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mappingInquiry")
public class InquiryAttach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiryAtt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;
}
