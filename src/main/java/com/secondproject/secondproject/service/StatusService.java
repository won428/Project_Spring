package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.Entity.StudentRecord;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.StudentRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class StatusService {

    @Autowired
    private StudentRecordRepository studentRecordRepository;

    // 학적 변경 신청 저장
    public void changeStatusWithEvidence(StatusChangeRequestDto dto) {
        if (dto.getUserType() != UserType.STUDENT) {
            throw new IllegalArgumentException("학생만 학적 변경 신청이 가능합니다.");
        }
        StudentRecord record = new StudentRecord();
        record.setTitle(dto.getTitle());
        record.setContent(dto.getContent());
        record.setApplied_date(dto.getAppliedDate());
        record.setProcessed_date(dto.getProcessedDate());
        record.setStatus(dto.getProcessStatus());
        record.setStudentStatus(dto.getAcademicRequest());
        // 유저, statusId 등 연관 엔티티 처리 필요시 추가
        studentRecordRepository.save(record);
    }

    // PK로 단일 학적변경신청 조회
    public StudentRecord getRecordById(Long recordId) {
        return studentRecordRepository.findByRecordId(recordId);
    }

    // 사용자별 학적변경 이력 목록 조회
    public List<StudentRecord> getRecordsByUserId(Long userId) {
        return studentRecordRepository.findByUserId(userId);
    }

    // 처리상태별 전체 학적변경신청 조회
    public List<StudentRecord> getRecordsByStatus(StatusRecords status) {
        return studentRecordRepository.findByStatus(status);
    }

    // 제목 등 부분검색용
    public List<StudentRecord> findRecordsByTitle(String keyword) {
        return studentRecordRepository.findByTitleContaining(keyword);
    }

    // 신청일 기간별 조회
    public List<StudentRecord> getRecordsByAppliedDateBetween(LocalDate start, LocalDate end) {
        return studentRecordRepository.findByAppliedDateBetween(start, end);
    }
}
