package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.SubmitAsgmt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmitAsgmtRepository extends JpaRepository<SubmitAsgmt, Long> {
    List<SubmitAsgmt> findByAssignmentId(long id);

    SubmitAsgmt findByUserId(Long id);
}
