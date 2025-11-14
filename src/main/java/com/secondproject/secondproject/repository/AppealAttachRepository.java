package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Mapping.AppealAttach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppealAttachRepository extends JpaRepository<AppealAttach, Long> {

    // 특정 Appeal에 연결된 Attachment 조회
    @Query("SELECT a FROM AppealAttach a WHERE a.appeal.id = :appealId")
    List<AppealAttach> findAttachmentsByAppealId(@Param("appealId") Long appealId);

}
