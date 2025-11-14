package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.StatusRecords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatusRecordsRepository extends JpaRepository<StatusRecords, Long> {

    Optional<StatusRecords> findByUserId(Long userId);


    StatusRecords findByUser_id(Long id);
}