package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {


    Page<Assignment> findByLectureId(Long id, Pageable pageable);

    Page<Assignment> findByLectureIdIn(List<Long> lectures, Pageable pageable);
}
