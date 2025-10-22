package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollegeRepository extends JpaRepository<College, Long> {
    College findCollegeById(Long id);

    boolean existsById(Long collegeId);

    // 변수명 카멜케이스로 변경시 @Query 삭제
    @Query("select c from College c order by c.c_type asc")
    List<College> findAllOrderByCTypeAsc();
}
