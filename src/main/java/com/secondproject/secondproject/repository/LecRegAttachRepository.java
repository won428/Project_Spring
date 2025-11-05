package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Mapping.LecRegAttach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LecRegAttachRepository extends JpaRepository<LecRegAttach, Long> {
    List<LecRegAttach> findByLecture_id(Long id);
}
