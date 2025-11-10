package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.StatusChangeListDto;
import com.secondproject.secondproject.dto.UpdateStatusDto;
import com.secondproject.secondproject.dto.UserDto;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.entity.StudentRecord;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.StatusChangeRepository;
import com.secondproject.secondproject.repository.StatusRecordsRepository;
import com.secondproject.secondproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final UserRepository userRepository;
    private final StatusChangeRepository statusChangeRepository;
    private final StatusRecordsRepository statusRecordsRepository;

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
        for (StudentRecord studentRecord : studentRecords) {
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

    /*관리자 학생 학적 변경용*/


    /*관리자 학생 학적 변경용*/
    /** 학적 정보 조회 */
    public UpdateStatusDto getStudentStatus(Long userId) {
        Optional<StatusRecords> statusOpt = statusRecordsRepository.findByUserId(userId);
        if (statusOpt.isEmpty()) return null;

        StatusRecords status = statusOpt.get();
        UpdateStatusDto dto = new UpdateStatusDto();
        dto.setStudentStatus(status.getStudentStatus());
        dto.setLeaveDate(status.getLeaveDate());
        dto.setRetentionDate(status.getRetentionDate());
        dto.setReturnDate(status.getReturnDate());
        dto.setGraduationDate(status.getGraduationDate());

        return dto;
    }

    /** 학적 정보 신규 생성 */
    public UpdateStatusDto createStudentStatus(Long userId, UpdateStatusDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        StatusRecords status = new StatusRecords();
        status.setUser(user);
        status.setStudentStatus(dto.getStudentStatus());
        status.setLeaveDate(dto.getLeaveDate());
        status.setRetentionDate(dto.getRetentionDate());
        status.setReturnDate(dto.getReturnDate());
        status.setGraduationDate(dto.getGraduationDate());

        statusRecordsRepository.save(status);
        return dto;
    }

    /** 학적 정보 수정 */
    public UpdateStatusDto updateStudentStatus(Long userId, UpdateStatusDto dto) {
        StatusRecords status = statusRecordsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("학적 정보가 존재하지 않습니다."));

        // DTO에 있는 필드만 업데이트
        if (dto.getStudentStatus() != null) {
            status.setStudentStatus(dto.getStudentStatus());
        }
        status.setLeaveDate(dto.getLeaveDate());
        status.setRetentionDate(dto.getRetentionDate());
        status.setReturnDate(dto.getReturnDate());
        status.setGraduationDate(dto.getGraduationDate());

        statusRecordsRepository.save(status);
        return dto;
    }

    /** 학적 정보 삭제 */
    public void deleteStudentStatus(Long userId) {
        StatusRecords status = statusRecordsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("학적 정보가 존재하지 않습니다."));
        statusRecordsRepository.delete(status);
    }

    public List<UserDto> getStudentsForManagement() {
        List<User> students = userRepository.findAllByType(UserType.STUDENT);

        return students.stream()
                .map(u -> {
                    UserDto dto = new UserDto();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setMajorName(u.getMajor() != null ? u.getMajor().getName() : "");
                    dto.setType(u.getType());
                    return dto;
                })
                .toList();
    }

    /**
     * PENDING 상태 StudentRecord 목록을 StatusChangeRequestDto로 반환
     */
    public List<StatusChangeRequestDto> getPendingStudentRecords() {
        List<StudentRecord> pendingRecords = statusChangeRepository.findByStatus(Status.PENDING);

        return pendingRecords.stream()
                .map(record -> new StatusChangeRequestDto(record, null)) // Attachment는 필요 시 연결
                .collect(Collectors.toList());
    }

    /** 학적 변경 신청 승인/거부 처리 */
    public void approveOrRejectStatus(Long recordId, Status status) {
        StudentRecord record = statusChangeRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 학생 신청 기록이 없습니다."));

        record.setStatus(status);  // enum Status 적용
        statusChangeRepository.save(record);
    }
}
