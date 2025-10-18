//package com.secondproject.secondproject.repository;
//
//import com.secondproject.secondproject.Entity.StatusRecords;
//import com.secondproject.secondproject.Entity.StudentRecord;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {
//
//    // PK로 단일 학적변경신청 조회
//    StudentRecord findByRecordId(Long recordId);
//
//    // 사용자별 학적변경 이력 목록 조회
//    List<StudentRecord> findByUserId(Long userId);
//
//    // 처리상태별 전체 학적변경신청 조회
//    List<StudentRecord> findByStatus(StatusRecords status);
//
//    // 제목 등 부분검색용
//    List<StudentRecord> findByTitleContaining(String keyword);
//
//    // 필요시 신청일/처리일 등 날짜로 필터
//    List<StudentRecord> findByAppliedDateBetween(LocalDate start, LocalDate end);
//
//    // 기타 필요에 따라 쿼리 메서드 확장 가능, 서비스 개발 시 필요하면 호출
//}
