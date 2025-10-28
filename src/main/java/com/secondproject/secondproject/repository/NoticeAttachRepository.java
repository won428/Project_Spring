package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Mapping.NoticeAttach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeAttachRepository extends JpaRepository<NoticeAttach, Long> {
    List<NoticeAttach> findByLecNoticeId(Long id);
}
