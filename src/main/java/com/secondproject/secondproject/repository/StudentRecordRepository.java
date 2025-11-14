package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {
    List<StudentRecord> findAllByUser_Id(Long id);
}
