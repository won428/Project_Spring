package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Entity.Attachment;
import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Entity.StudentRecord;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.repository.AttachmentRepository;
import com.secondproject.secondproject.repository.StatusChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StatusService {

    @Autowired
    private StatusChangeRepository statusChangeRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    // 학적 변경 신청 저장 (처리 상태 지정 없이 기본 저장)
    public void changeStatusWithEvidence(StatusChangeRequestDto dto) {
        // 학생 여부 검증은 컨트롤러 등 상위 계층에서 처리한다고 가정

        StudentRecord record = new StudentRecord();
        record.setTitle(dto.getTitle());
        record.setContent(dto.getContent());
        record.setApplied_date(dto.getAppliedDate());
        record.setProcessed_date(dto.getProcessedDate());
        record.setStudentStatus(dto.getAcademicRequest());
        // 신청자 ID를 그대로 기록
        record.setUser(dto.getUser());

        // 학적 변경 신청 기본 저장
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

    // PK로 단일 학적변경신청 상세내역 조회
    public StatusChangeRequestDto getChangeRequestDetail(Long recordId) {
        Optional<StatusRecords> optionalRecord = statusChangeRepository.findById(recordId);
        if (optionalRecord.isEmpty()) {
            return null; // 또는 적절한 예외 처리
        }
        StatusRecords record = optionalRecord.get();

        StatusChangeRequestDto dto = new StatusChangeRequestDto();
        dto.setRecordId(record.getId());                  // PK (status_id)
        dto.setUser(record.getUser());            // 신청자 ID
        dto.setStatusId(record.getId());                   // 참조용 상태 ID (recordId 재사용)
        dto.setTitle(null);                                // title 필드가 없으므로 null 설정
        dto.setContent(null);                              // content 필드가 없으므로 null 설정
        dto.setAppliedDate(record.getAdmissionDate());     // 입학일
        dto.setProcessedDate(null);                        // 처리일 엔티티에 없으면 null
        dto.setAcademicRequest(record.getStudent_status()); // 학생 상태(enum)
        dto.setAttachmentId(null);                         // 첨부파일ID는 별도 필드/관계 필요 시 처리

        // 필요하면 아래와 같이 추가 상세 필드도 DTO에 매핑 가능
        dto.setLeaveDate(record.getLeaveDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setGraduationDate(record.getGraduation_date());
        dto.setRetentionDate(record.getRetention_date());
        dto.setExpelledDate(record.getExpelled_date());
        dto.setTotalCredit(record.getTotalCredit());
        dto.setCurrentCredit(record.getCurrentCredit());
        dto.setStudentImage(record.getStudent_image());

        return dto;
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
