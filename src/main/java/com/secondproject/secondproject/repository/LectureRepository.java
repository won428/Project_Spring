package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
}
