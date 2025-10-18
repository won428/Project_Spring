package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Entity.Attachment;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.Entity.StudentRecord;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.AttachmentRepository;
import com.secondproject.secondproject.repository.StatusChangeRepository;
/*import com.secondproject.secondproject.repository.StudentRecordRepository;
    추가 구현 메소드 필요 시 재활성화
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class StatusService {

    @Autowired
    private StatusChangeRepository statusChangeRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    // 학적 변경 신청 저장 (처리 상태 지정 없이 기본 저장)
    public void changeStatusWithEvidence(StatusChangeRequestDto dto) {
        if (dto.getUserType() != UserType.STUDENT) {
            throw new IllegalArgumentException("학생만 학적 변경 신청이 가능합니다.");
        }
        StudentRecord record = new StudentRecord();
        record.setTitle(dto.getTitle());
        record.setContent(dto.getContent());
        record.setApplied_date(dto.getAppliedDate());
        record.setProcessed_date(dto.getProcessedDate());
        record.setStudentStatus(dto.getAcademicRequest());
        // 처리 상태 없이 단순 저장 (승인/거부 상태는 별도 관리)
        StatusChangeRepository.save(record);
    }

    // 특정 학생의 학적변경신청 상태(PENDING, APPROVED, REJECTED) 목록 조회
    public List<StudentRecord> getStudentChangeRequestsStatus(Long userId) {
        // userId가 학생인지 미리 확인해도 좋음

        // 상태 세 가지만 필터링해 조회 (임의 메서드명, 구현된 리포지토리 메서드 필요)
        List<Status> validStatuses = List.of(Status.PENDING, Status.APPROVED, Status.REJECTED);

        return statusChangeRepository.findByUserIdAndStatusIn(userId, validStatuses);
    }

    // 간단 첨부파일 저장 (파일명만 저장)
    public void storeAttachmentFile(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            Attachment attachment = new Attachment();
            attachment.setName(originalFileName); // 필드명이 'name' 으로 정의되어 있음
            attachment.setStored_key(originalFileName); // 변환된 파일명 (추후 수정 필요)
            attachment.setContent_type(file.getContentType());
            attachment.setSize_bytes(file.getSize());
            attachment.setUpload_at(LocalDate.now());
            AttachmentRepository.fSave(attachment); // fSave → save 로 수정
        } catch (Exception e) {
            throw new RuntimeException("첨부파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // 추가 구현 가능한 메소드 목록

    // PK로 단일 학적변경신청 조회
//    public StudentRecord getRecordById(Long recordId) {
//        return studentRecordRepository.findByRecordId(recordId);
//    }
//
//    // 사용자별 학적변경 이력 목록 조회
//    public List<StudentRecord> getRecordsByUserId(Long userId) {
//        return studentRecordRepository.findByUserId(userId);
//    }
//
//    // 제목 검색용
//    public List<StudentRecord> findRecordsByTitle(String keyword) {
//        return studentRecordRepository.findByTitleContaining(keyword);
//    }
//
//    // 신청일 기간별 조회
//    public List<StudentRecord> getRecordsByAppliedDateBetween(LocalDate start, LocalDate end) {
//        return studentRecordRepository.findByAppliedDateBetween(start, end);
//    }
}
