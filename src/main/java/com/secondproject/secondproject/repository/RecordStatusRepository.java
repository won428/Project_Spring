package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Entity.StatusRecords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordStatusRepository extends JpaRepository<StatusRecords, Long> {
    // 컬럼명이 status_id라면 아래 메서드명은 camelCase로 맞춤
    Optional<StatusRecords> findById(Long id);

    // 기존 getStatusRecordById는 제거
}