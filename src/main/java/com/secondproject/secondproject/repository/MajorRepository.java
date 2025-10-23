package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major,Long> {
    List<Major> findByCollege_Id(Long collegeId);

    Major findMajorById(Long id);
}
