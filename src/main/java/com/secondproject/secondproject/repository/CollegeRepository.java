package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollegeRepository extends JpaRepository<College, Long> {
    College findCollegeById(Long id);

    boolean existsById(Long collegeId);


    List<College> findAllByOrderByTypeAsc();
}
