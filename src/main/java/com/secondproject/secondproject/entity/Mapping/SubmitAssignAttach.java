package com.secondproject.secondproject.entity.Mapping;

import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.SubmitAsgmt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MappingSubmit")
public class SubmitAssignAttach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MapSubmit_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_id")
    private SubmitAsgmt submitAsgmt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;


}
