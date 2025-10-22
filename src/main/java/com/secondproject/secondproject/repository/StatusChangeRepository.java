package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Entity.StudentRecord;
import com.secondproject.secondproject.Enum.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusChangeRepository extends JpaRepository<StatusRecords, Long> {
    static void save(StudentRecord record) {
    }
    // JpaRepository의 save() 메서드를 사용하면 기본 저장 기능이 작동함

    // 커스텀 메서드를 쓰려면 아래처럼 선언만 해두고 구현은 필요
    // void saveStatusChange(StatusChangeRequestDto dto);

    @Query("select s from StudentRecord s where s.user.id = :userId and s.status in :statuses")
    List<StudentRecord> findByUserIdAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<Status> statuses);

}