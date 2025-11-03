package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRegRepository extends JpaRepository<CourseRegistration, Long> {
    List<CourseRegistration> findByUser_Id(Long userId);

    boolean existsByUser_IdAndLecture_Id(Long userId, Long lecId);

    CourseRegistration findByLecture_Id(Long lectureId);

    List<CourseRegistration> findAllByLecture_IdAndStatus(Long lectureId, Status status);


    List<CourseRegistration> findAllByLecture_Id(Long lectureId);

    Long countByLecture_IdAndStatus(Long id, Status status);
}
