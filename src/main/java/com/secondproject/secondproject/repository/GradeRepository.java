package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByUser_IdAndLecture_IdAndLectureGrade(Long userId, Long lectureId, String lectureGrade);
}
