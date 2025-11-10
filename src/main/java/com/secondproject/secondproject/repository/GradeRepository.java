package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.math.BigDecimal;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByUser_IdAndLecture_IdAndLectureGrade(Long userId, Long lectureId, BigDecimal lectureGrade);

    @Query("SELECT g FROM Grade g JOIN FETCH g.lecture l WHERE g.user.id = :userId")
    List<Grade> findByUserId(@Param("userId") Long userId);


    List<Grade> findAllByLecture_Id(Long id);
    boolean existsByLecture_IdAndUser_Id(Long lectureId, Long userId);
}
