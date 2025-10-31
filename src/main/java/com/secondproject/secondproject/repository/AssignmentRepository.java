package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {


    Page<Assignment> findByLectureId(Long id, Pageable pageable);

    Assignment findByLectureId(Long id);
}
