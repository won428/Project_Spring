package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepository extends JpaRepository<College,Long> {
    boolean existsById(Long collegeId);
}
