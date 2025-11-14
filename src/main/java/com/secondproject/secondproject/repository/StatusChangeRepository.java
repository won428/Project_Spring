package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusChangeRepository extends JpaRepository<StudentRecord, Long> {

    Optional<StudentRecord> findById(Long id);

    void deleteById(Long id);

    // 최신순 목록(스프링 자동 구현)
    List<StudentRecord> findByUserIdOrderByIdDesc(Long userId);

    List<StudentRecord> findAllByUser_Id(Long id);

    // PENDING 상태의 StudentRecord 전체 조회
    List<StudentRecord> findByStatus(Status status);

    // default save(...)는 제거. JpaRepository.save 사용.
}


    // 커스텀 메서드를 쓰려면 아래처럼 선언만 해두고 구현은 필요
    // void saveStatusChange(StatusChangeRequestDto dto);
