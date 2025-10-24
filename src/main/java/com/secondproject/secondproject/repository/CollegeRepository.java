package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.College;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CollegeRepository extends JpaRepository<College, Long>, JpaSpecificationExecutor<College> {
    College findCollegeById(Long id);

    boolean existsById(Long collegeId);


    List<College> findAllByOrderByTypeAsc();

    Page<College> findAll(Specification<College> spec, Pageable pageable);

    // Type컬럼이 keyword를 부분 포함 + 대소문자 무시, 반환값이 pageable
    Page<College> findByTypeContainingIgnoreCase(String keyword, Pageable pageable);

    //Office이 keyword를 부분 포함
    Page<College> findByOfficeContaining(String keyword, Pageable pageable);

    // Type이 kw1을 대소문자 무시하고 부분 포함 또는 Office가 kw2를 부분 포함
    Page<College> findByTypeContainingIgnoreCaseOrOfficeContaining(String keyword1, String keyword2, Pageable pageable);
}