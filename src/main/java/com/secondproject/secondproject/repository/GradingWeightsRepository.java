package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.GradingWeights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface GradingWeightsRepository extends JpaRepository<GradingWeights, Long> {
    @Query("select g.attendanceScore from GradingWeights g where g.lecture.id = :lectureId")
    Optional<BigDecimal> findAttendanceRatioByLectureId(@Param("lectureId") Long lectureId);

    Optional<GradingWeights> findByLectureId(Long lectureId);

    GradingWeights findByLecture_Id(Long id);

    void deleteByLecture_Id(Long id);
}
