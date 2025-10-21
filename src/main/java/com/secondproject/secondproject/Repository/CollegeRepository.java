package com.secondproject.secondproject.Repository;

import com.secondproject.secondproject.Entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepository extends JpaRepository<College, Long> {
}
