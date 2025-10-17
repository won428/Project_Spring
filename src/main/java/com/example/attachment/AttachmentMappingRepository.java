
package com.example.attachment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentMappingRepository extends JpaRepository<AttachmentMapping, Long> {
    List<AttachmentMapping> findByTargetTypeAndTargetId(TargetType targetType, Long targetId);
}
