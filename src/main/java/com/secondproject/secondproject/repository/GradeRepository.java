package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByUser_IdAndLecture_IdAndLectureGrade(Long userId, Long lectureId, BigDecimal lectureGrade);

    List<Grade> findAllByLecture_Id(Long id);
    boolean existsByLecture_IdAndUser_Id(Long lectureId, Long userId);
}
