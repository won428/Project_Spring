package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.StatusChangeListDto;
import com.secondproject.secondproject.entity.StudentRecord;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.StatusChangeRepository;
import com.secondproject.secondproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final UserRepository userRepository;
    private final StatusChangeRepository statusChangeRepository;

    // 1) 생성
    @Transactional
    public StatusChangeRequestDto createChangeRequest(StatusChangeRequestDto dto) {
        // 사용자 조회
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUserId()));

        // 엔티티 조립
        StudentRecord record = new StudentRecord();
        record.setUser(user);
        record.setStudentStatus(dto.getStudentStatus());
        record.setTitle(dto.getTitle());
        record.setContent(dto.getContent());
        record.setAppliedDate(dto.getAppliedDate());
        record.setStatus(dto.getStatus());

        // 서버 보정
        record.setAppliedDate(LocalDate.now());
        record.setProcessedDate(null);
        record.setStatus(Status.PENDING);

        // 저장
        StudentRecord saved = statusChangeRepository.save(record);

        // 응답 DTO
        StatusChangeRequestDto res = new StatusChangeRequestDto();
        res.setRecordId(saved.getId());
        res.setUserId(user.getId());
        res.setStudentStatus(saved.getStudentStatus());
        res.setTitle(saved.getTitle());
        res.setContent(saved.getContent());
        res.setAppliedDate(saved.getAppliedDate());
        res.setProcessedDate(saved.getProcessedDate());
        res.setStatus(saved.getStatus());
        res.setAttachmentId(dto.getAttachmentId());
        return res;
    }

    // 3) 상세
    @Transactional(readOnly = true)
    public StatusChangeRequestDto getChangeRequestDetail(Long recordId) {
        Optional<StudentRecord> opt = statusChangeRepository.findById(recordId);
        if (opt.isEmpty()) return null;
        StudentRecord sr = opt.get();

        StatusChangeRequestDto d = new StatusChangeRequestDto();
        d.setRecordId(sr.getId());
        d.setUserId(sr.getUser() != null ? sr.getUser().getId() : null);
        d.setStudentStatus(sr.getStudentStatus());
        d.setTitle(sr.getTitle());
        d.setContent(sr.getContent());
        d.setAppliedDate(sr.getAppliedDate());
        d.setProcessedDate(sr.getProcessedDate());
        d.setStatus(sr.getStatus());
        // 필요 시 첨부 id 매핑: d.setAttachmentId(sr.getAttachment() != null ? sr.getAttachment().getId() : null);
        return d;
    }

    // 목록 조회 조장님이 작성해줌
    public List<StatusChangeListDto> findMyList(Long id) {
        List<StudentRecord> studentRecords = this.statusChangeRepository.findAllByUser_Id(id);
        List<StatusChangeListDto> statusChangeListDtos = new ArrayList<>();
        for(StudentRecord studentRecord : studentRecords){
            StatusChangeListDto statusChangeListDto = new StatusChangeListDto();

            statusChangeListDto.setRecordId(studentRecord.getId());
            statusChangeListDto.setTitle(studentRecord.getTitle());
            statusChangeListDto.setAppliedDate(studentRecord.getAppliedDate());
            statusChangeListDto.setProcessedDate(studentRecord.getProcessedDate());
            statusChangeListDto.setStudentStatus(studentRecord.getStudentStatus());
            statusChangeListDto.setStatus(studentRecord.getStatus());


            statusChangeListDtos.add(statusChangeListDto);
        }

        return statusChangeListDtos;
    }

    // 학적변경신청 리스트 삭제기능
    public boolean deleteChangeRequest(Long id) {
        Optional<StudentRecord> record = statusChangeRepository.findById(id);
        if (record.isPresent()) {
            statusChangeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public StatusChangeRequestDto updateChangeRequest(Long recordId, StatusChangeRequestDto dto) {
        StudentRecord record = statusChangeRepository.findById(recordId).get();

        // 수정할 데이터 반영 (필수 필드만 간략하게)
        record.setStudentStatus(dto.getStudentStatus());
        record.setTitle(dto.getTitle());
        record.setContent(dto.getContent());
        record.setAppliedDate(dto.getAppliedDate());
        record.setStatus(dto.getStatus()); // 필요시

        StudentRecord savedRecord = statusChangeRepository.save(record);

        // DTO로 변환 후 반환
        StatusChangeRequestDto resultDto = new StatusChangeRequestDto();
        resultDto.setRecordId(savedRecord.getId());
        resultDto.setUserId(savedRecord.getUser().getId());
        resultDto.setStudentStatus(savedRecord.getStudentStatus());
        resultDto.setTitle(savedRecord.getTitle());
        resultDto.setContent(savedRecord.getContent());
        resultDto.setAppliedDate(savedRecord.getAppliedDate());
        resultDto.setProcessedDate(savedRecord.getProcessedDate());
        resultDto.setStatus(savedRecord.getStatus());
        return resultDto;
    }

}
