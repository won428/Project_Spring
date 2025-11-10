package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.dto.LectureScheduleDto;
import com.secondproject.secondproject.entity.LectureSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.List;

public interface LecScheduleRepository extends JpaRepository<LectureSchedule, Long> {
    List<LectureSchedule> findAllByLecture_Id(Long id);
    List<LectureSchedule> findByLecture_Id(Long lectureId);

    void deleteAllByLecture_Id(Long id);
}
