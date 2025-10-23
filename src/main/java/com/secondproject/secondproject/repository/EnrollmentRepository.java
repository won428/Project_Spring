package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.Enum.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // 만약 Enrollment가 User를 참조하고 User에 type 필드가 있다면, JPQL 직접 작성

    @Query("SELECT e FROM Enrollment e WHERE e.lecture.id = :lectureId AND e.user.type = :userType")
    List<Enrollment> findByLectureIdAndType(@Param("lecture_id") Long id, @Param("u_Type") UserType type);



}

