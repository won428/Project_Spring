package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRegRepository extends JpaRepository<CourseRegistration, Long> {
}
