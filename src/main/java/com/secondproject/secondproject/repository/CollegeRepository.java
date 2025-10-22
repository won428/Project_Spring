package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepository extends JpaRepository<College, Long> {
    College findCollegeById(Long id);
}
