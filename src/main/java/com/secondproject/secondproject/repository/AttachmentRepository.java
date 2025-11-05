package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    static void fSave(Attachment attachment) {

    }


    Optional<Attachment> findByStoredKey(String storedKey);
}
