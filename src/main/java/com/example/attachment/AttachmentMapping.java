
package com.example.attachment;

import jakarta.persistence.*;

@Entity
public class AttachmentMapping {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private Long targetId;

    public AttachmentMapping() {}
    public AttachmentMapping(Attachment attachment, TargetType targetType, Long targetId) {
        this.attachment = attachment;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public Long getId() { return id; }
    public Attachment getAttachment() { return attachment; }
}
