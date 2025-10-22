package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.Enum.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e.user FROM Enrollment e WHERE e.lecture.id = :lectureId AND e.user.u_type = :studentType")
    List<User> findStudentsByLectureIdAndUserType(@Param("lectureId") Long lectureId,
                                                  @Param("studentType") UserType studentType);
}

