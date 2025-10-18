package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Entity.StatusRecords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordStatusRepository extends JpaRepository<StatusRecords, Long>{
    StatusRecords getStatusRecordById(Long statusId);

    Optional<StatusRecords> findBySid(Long userId);
}
