package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusChangeRepository extends JpaRepository<StudentRecord, Long> {

    // 최신순 목록(스프링 자동 구현)
    List<StudentRecord> findByUserIdOrderByIdDesc(Long userId);

    // default save(...)는 제거. JpaRepository.save 사용.
}


    // 커스텀 메서드를 쓰려면 아래처럼 선언만 해두고 구현은 필요
    // void saveStatusChange(StatusChangeRequestDto dto);
