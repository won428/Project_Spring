package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnlineLectureRepository extends JpaRepository<OnlineLecture, Long> {
}
