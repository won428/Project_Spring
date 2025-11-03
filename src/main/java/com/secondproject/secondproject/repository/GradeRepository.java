package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    @Query("SELECT g FROM Grade g JOIN FETCH g.lecture l WHERE g.user.id = :userId")
    List<Grade> findByUserId(@Param("userId") Long userId);
}
