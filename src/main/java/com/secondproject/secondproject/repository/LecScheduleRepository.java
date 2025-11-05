package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.dto.LectureScheduleDto;
import com.secondproject.secondproject.entity.LectureSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LecScheduleRepository extends JpaRepository<LectureSchedule, Long> {
    List<LectureSchedule> findAllByLecture_Id(Long id);
}
