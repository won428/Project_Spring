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
//    private final AttachmentRepository attachmentRepository;

    // 파일 저장: 파일을 저장하고 첨부 PK를 반환
//    @Transactional
//    public Long storeAttachmentFile(MultipartFile file) {
//        if (file == null || file.isEmpty()) return null;
//
//        try {
//            // 예시: 로컬 디스크 저장
//            String uuid = java.util.UUID.randomUUID().toString();
//            String ext = Optional.ofNullable(file.getOriginalFilename())
//                    .filter(n -> n.contains("."))
//                    .map(n -> n.substring(n.lastIndexOf('.') + 1))
//                    .orElse("");
//            String storedKey = uuid + (ext.isEmpty() ? "" : "." + ext);
//
//            java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads");
//            java.nio.file.Files.createDirectories(uploadDir);
//            java.nio.file.Path target = uploadDir.resolve(storedKey);
//            java.nio.file.Files.copy(file.getInputStream(), target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
//
//            Attachment att = new Attachment();
//            // 엔티티 실제 필드명에 맞게 변경 필요
//            att.setName(file.getOriginalFilename());
//            att.setContentType(file.getContentType());
//            att.setSizeBytes(file.getSize());
//            att.setStoredKey(storedKey); // NOT NULL 컬럼 채우기
//
//            Attachment saved = attachmentRepository.save(att);
//            return saved.getId();
//        } catch (Exception ex) {
//            throw new RuntimeException("Attachment store failed", ex);
//        }
//    }


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

        // 첨부 연결(선택)
//        if (dto.getAttachmentId() != null) {
//            Attachment att = attachmentRepository.findById(dto.getAttachmentId())
//                    .orElseThrow(() -> new IllegalArgumentException("Attachment not found: " + dto.getAttachmentId()));
//            // 연관관계가 있다면 주입(예: record.setAttachment(att))
//        }

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

    // 2) 목록
    @Transactional(readOnly = true)
    public List<StatusChangeRequestDto> getStudentChangeRequests(Long userId) {
        List<StudentRecord> list = statusChangeRepository.findByUserIdOrderByIdDesc(userId);
        return list.stream().map(sr -> {
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
        }).collect(java.util.stream.Collectors.toList());
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

//    @Transactional
//    public StatusChangeRequestDto createChangeRequestByEmail(String email, StatusChangeRequestDto dto) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found by email: " + email));
//        dto.setUserId(user.getId());
//        return createChangeRequest(dto); // 기존 메서드 재사용
//    }
}
