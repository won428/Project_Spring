package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.StatusRecords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRecordsRepository extends JpaRepository<StatusRecords, Long> {
    StatusRecords findByUser_id(Long id);
}