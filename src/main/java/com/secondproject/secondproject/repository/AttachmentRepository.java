package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    static void fSave(Attachment attachment) {

    }
}
