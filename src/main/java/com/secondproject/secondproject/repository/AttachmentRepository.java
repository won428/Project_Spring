package com.secondproject.secondproject.Repository;

import com.secondproject.secondproject.Entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    static void fSave(Attachment attachment) {

    }
}
