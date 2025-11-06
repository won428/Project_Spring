package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Mapping.LecRegAttach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface LecRegAttachRepository extends JpaRepository<LecRegAttach, Long> {
    List<LecRegAttach> findByLecture_id(Long id);

    @Query("select lra.attachment.id from LecRegAttach lra where lra.lecture.id = :lectureId")
    List<Long> findAttachmentIdsByLectureId(@Param("lectureId") Long lectureId);

    @Modifying
    @Query("delete from LecRegAttach lra where lra.lecture.id = :lectureId and lra.attachment.id = :attachmentId")
    int deleteByLectureIdAndAttachmentId(Long lectureId, Long attachmentId);


}
