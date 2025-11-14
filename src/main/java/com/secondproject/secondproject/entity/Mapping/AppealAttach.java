package com.secondproject.secondproject.entity.Mapping;


import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Inquiry;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mappingAppeal")
public class AppealAttach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appealAtt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeal_id", nullable = false)
    private Appeal appeal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;
}
