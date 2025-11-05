package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OnlineLectureRepository extends JpaRepository<OnlineLecture, Long> {
    Optional<OnlineLecture> findByLectureId(Long id);

    Page<OnlineLecture> findByLectureId(Long id, Pageable pageable);
}
