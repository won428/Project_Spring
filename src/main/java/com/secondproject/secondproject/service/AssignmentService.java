package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.AssignmentAttach;
import com.secondproject.secondproject.entity.Mapping.NoticeAttach;
import com.secondproject.secondproject.entity.Mapping.SubmitAssignAttach;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final AssignmentAttachRepository assignmentAttachRepository;
    private final LectureRepository lectureRepository;
    private final SubmitAsgmtRepository submitAsgmtRepository;
    private final SubmitAssignAttachRepository submitAssignAttachRepository;

    @Transactional
    public void insertAttachment(AssignmentInsertDto assignmentDto, List<MultipartFile> files) throws IOException {
        if (assignmentDto.getUsername() == null || assignmentDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("사용자가 없습니다.");
        }
        Long userCode = Long.parseLong(assignmentDto.getUsername());
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Lecture lecture = lectureRepository.findById(assignmentDto.getLectureId())
                .orElseThrow(() -> new EntityNotFoundException("강의명이 일치하지 않습니다."));


        Assignment assignInsert = new Assignment();
        assignInsert.setUser(user);
        assignInsert.setTitle(assignmentDto.getTitle());
        assignInsert.setContent(assignmentDto.getContent());
        assignInsert.setOpenAt(assignmentDto.getOpenAt());
        assignInsert.setDueAt(assignmentDto.getDueAt());
        assignInsert.setLecture(lecture);
        Assignment assignment = assignmentRepository.save(assignInsert);

        List<MultipartFile> safeFiles = (files == null)
                ? java.util.Collections.emptyList()
                : files.stream().filter(f -> f != null && !f.isEmpty()).toList();

        if (safeFiles.isEmpty()) {
            return;
        }
        for (MultipartFile file : safeFiles) {
            Attachment attachment = attachmentService.save(file, user);
            AssignmentAttach saved = new AssignmentAttach();
            saved.setAttachment(attachment);
            saved.setAssignment(assignment);
            assignmentAttachRepository.save(saved);
        }
    }


    public Page<AssignmentDto> getPagedNotices(Long id, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by((Sort.Direction.DESC), "createAt"));
        Page<Assignment> result = assignmentRepository.findByLectureId(id, pageable);
        return result.map(AssignmentDto::fromEntity);
    }

    public AssignmentResDto findById(Long id, String username) {
        Long userCode = Long.parseLong(username);
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없다!"));
        //과제 매핑 테이블에서 id로 찾기

        //제출된 과제 매핑 테이블에서 제출된 과제들 찾기

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("과제 없다."));

        List<AssignmentAttach> assignmentAttaches = assignmentAttachRepository.findByAssignment_Id(id);
        // 과제 공지 파일 목록
        List<Attachment> attachments = assignmentAttaches.stream()
                .map(AssignmentAttach::getAttachment)
                .toList();
        //과제 제출 목록 -  교수
        List<SubmitAsgmt> allSubmissions = submitAsgmtRepository.findByAssignmentId(assignment.getId());


        List<SubmitAssignAttach> allSubmittedAttaches = submitAssignAttachRepository.findByAssignment_Id(id);

        if (user.getType().equals(UserType.STUDENT)) {
            //제출내역
            SubmitAsgmt submittedOne = allSubmissions.stream()
                    .filter(s -> s.getUser().getId().equals(user.getId()))
                    .findAny()
                    .orElse(null);

            List<Attachment> attachmentsForStudent;
            if (submittedOne != null) {
                // 5. 학생 본인의 첨부파일 목록 찾기
                attachmentsForStudent = allSubmittedAttaches.stream()
                        .filter(sa -> sa.getSubmitAsgmt().getId().equals(submittedOne.getId()))
                        .map(SubmitAssignAttach::getAttachment)
                        .toList();
            } else {
                attachmentsForStudent = Collections.emptyList();
            }

            // DTO 생성 (학생용)
            return AssignmentResDto.fromEntity(assignment, attachments, submittedOne, attachmentsForStudent);

        } else {
            Map<Long, List<Attachment>> attachmentsMap = allSubmittedAttaches.stream()
                    .collect(Collectors.groupingBy(
                            sa -> sa.getSubmitAsgmt().getId(), // SubmitAsgmt의 ID로 그룹화
                            Collectors.mapping(SubmitAssignAttach::getAttachment, Collectors.toList()) // Attachment만 리스트로
                    ));

            // 5. '모든' 제출물(allSubmissions)을 DTO 리스트로 변환
            List<SubmitAsgmtDto> submitAsgmtDtoList = allSubmissions.stream()
                    .map(submission -> {
                        // 맵에서 '이 제출물(submission)'의 첨부파일 목록을 찾음
                        List<Attachment> attachmentsForThisSubmission = attachmentsMap.getOrDefault(submission.getId(), Collections.emptyList());
                        return SubmitAsgmtDto.fromEntity(submission, attachmentsForThisSubmission);
                    })
                    .toList();
            return (AssignmentResDto.fromProfessorEntity(assignment, attachments, submitAsgmtDtoList));
        }

    }

    public void updateAssignment(
            Long assignId,
            AssignSubmitInsertDto assignSubmitInsertDto,
            List<MultipartFile> files,
            List<String> existingFileKeys) throws IOException {
        Long userCode = Long.parseLong(assignSubmitInsertDto.getUsername());
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new EntityNotFoundException("유저 없음"));
        Assignment assignment = assignmentRepository.findById(assignId)
                .orElseThrow(() -> new EntityNotFoundException("과제 없음"));
        assignment.setTitle(assignSubmitInsertDto.getTitle());
        assignment.setContent(assignSubmitInsertDto.getContent());
        assignmentRepository.save(assignment);


        List<AssignmentAttach> attaches = assignmentAttachRepository.findByAssignment_Id(assignId);
        if (attaches != null && !attaches.isEmpty()) {
            assignmentAttachRepository.deleteAll(attaches);
        }

        List<MultipartFile> newValidFiles = (files == null)
                ? Collections.emptyList()
                : files.stream().filter(f -> f != null && !f.isEmpty()).toList();

        if (!newValidFiles.isEmpty()) {
            for (MultipartFile file : newValidFiles) { // 'newValidFiles' 사용
                Attachment attachment = attachmentService.save(file, user);
                AssignmentAttach assignmentAttach = new AssignmentAttach();
                assignmentAttach.setAssignment(assignment);
                assignmentAttach.setAttachment(attachment);
                assignmentAttachRepository.save(assignmentAttach);
            }
        }
        if ((existingFileKeys != null && !existingFileKeys.isEmpty())) {
            for (String storedKey : existingFileKeys) {
                Attachment existingAttachment = attachmentService.findByStoredKey(storedKey)
                        .orElseThrow(() -> new EntityNotFoundException("기존 파일 키를 찾을 수 없습니다: " + storedKey));
                AssignmentAttach reSaved = new AssignmentAttach();
                reSaved.setAssignment(assignment);
                reSaved.setAttachment(existingAttachment);
                assignmentAttachRepository.save(reSaved);
            }
        }
    }

    @Transactional
    public void deleteAssign(Long assignId) {
        Assignment assignment = assignmentRepository.findById(assignId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 assignment"));

        List<AssignmentAttach> attaches = assignmentAttachRepository.findByAssignment_Id(assignId);
        for (AssignmentAttach nn : attaches) {
            assignmentAttachRepository.deleteByAssignment(assignment);
            attachmentService.deleteById(nn.getAttachment().getId());

        }
        assignmentRepository.deleteById(assignId);


    }
}

