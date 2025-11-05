package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Mapping.BoardAttach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardAttachRepository extends JpaRepository<BoardAttach, Long> {
    List<BoardAttach> findByNoticeId(Long id);
}
