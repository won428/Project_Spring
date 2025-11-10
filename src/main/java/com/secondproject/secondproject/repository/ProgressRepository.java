package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.entity.UserLectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgressRepository extends JpaRepository<UserLectureProgress, Long> {
    Optional<UserLectureProgress> findByOnlineLecture(OnlineLecture onlineLecture);
}
