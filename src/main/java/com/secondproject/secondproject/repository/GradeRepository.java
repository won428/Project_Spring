package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Grade;
import com.secondproject.secondproject.service.GradeSummaryDto;
import jakarta.validation.constraints.NotNull;
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

    Grade findByUser_IdAndLecture_Id(Long userId, Long lectureId);

    List<Grade> findAllByLecture_Id(Long id);

    boolean existsByLecture_IdAndUser_Id(Long lectureId, Long userId);
    // 성적이 이미 저장된 학생인지 확인

    @Query("""
            select new com.secondproject.secondproject.service.GradeSummaryDto(
                  g.user.id, g.totalScore, g.lectureGrade
              )
              from Grade g
              where g.lecture.id = :lectureId
                and g.totalScore is not null
                and g.totalScore > 0
            """)
    List<GradeSummaryDto> findSummariesByLectureId(Long lectureId);

}
