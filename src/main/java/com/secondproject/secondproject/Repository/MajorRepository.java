package com.secondproject.secondproject.Repository;

import com.secondproject.secondproject.Entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major,Long> {
    List<Major> findByCollege_Id(Long collegeId);
}
