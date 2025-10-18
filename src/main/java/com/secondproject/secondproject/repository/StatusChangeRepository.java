package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusChangeRepository extends JpaRepository<StatusRecords, Long> {
    static void save(StudentRecord record) {
    }
    // JpaRepository의 save() 메서드를 사용하면 기본 저장 기능이 작동함

    // 커스텀 메서드를 쓰려면 아래처럼 선언만 해두고 구현은 필요
    // void saveStatusChange(StatusChangeRequestDto dto);
}