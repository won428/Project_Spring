package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.LectureSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LecScheduleRepository extends JpaRepository<LectureSchedule, Long> {
}
