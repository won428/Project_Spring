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
import java.util.List;

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
        if (assignmentDto.getEmail() == null || assignmentDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("사용자 이메일이 없습니다.");
        }
        System.out.println(assignmentDto.getEmail());
        User user = userRepository.findByEmail(assignmentDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));

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
        for (MultipartFile file : files) {
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

    public AssignmentResDto findById(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없다!"));
        //과제 매핑 테이블에서 id로 찾기
        List<AssignmentAttach> assignmentAttaches = assignmentAttachRepository.findByAssignment_Id(id);
        //제출된 과제 매핑 테이블에서 제출된 과제들 찾기
        List<SubmitAssignAttach> submittedAttaches = submitAssignAttachRepository.findByAssignment_Id(id);//교수

//        SubmitAsgmt AForStudent = submitAsgmtRepository.findByUserId(user.getId());
//        List<SubmitAssignAttach> SubmittedAForStudent = submitAssignAttachRepository.findBySubmitId(AForStudent.getId());


        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("과제 없다."));

        List<SubmitAsgmt> submitAsgmt = submitAsgmtRepository.findByAssignmentId(assignment.getId());

        // 과제 공지 파일 목록
        List<Attachment> attachments = assignmentAttaches.stream()
                .map(AssignmentAttach::getAttachment)
                .toList();
        //과제 제출 목록 -  교수

        List<Attachment> attachmentSubmitted = submittedAttaches.stream()
                .map(SubmitAssignAttach::getAttachment)
                .toList();


        if (user.getType().equals(UserType.STUDENT)) {
            //제출내역
            SubmitAsgmt submittedOne = submitAsgmt.stream()
                    .filter(s -> s.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .orElse(null);

            //자료
            List<Attachment> submittedA = attachmentSubmitted.stream()
                    .filter(s -> s.getUser().getId().equals(user.getId()))
                    .toList();

            return AssignmentResDto.fromEntity(assignment, attachments, submittedOne, submittedA);

        } else {

            return (AssignmentResDto.fromEntity(assignment, attachments, submitAsgmt, attachmentSubmitted));
        }

    }

    public void updateAssignment(
            Long assignId,
            AssignSubmitInsertDto assignSubmitInsertDto,
            List<MultipartFile> files) throws IOException {

        User user = userRepository.findByEmail(assignSubmitInsertDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("유저 없음"));
        Assignment assignment = assignmentRepository.findById(assignId)
                .orElseThrow(() -> new EntityNotFoundException("과제 없음"));
        assignment.setTitle(assignSubmitInsertDto.getTitle());
        assignment.setContent(assignSubmitInsertDto.getContent());
        assignmentRepository.save(assignment);


        if (files != null && !files.isEmpty() && files.get(0).getSize() > 0) {
            assignmentAttachRepository.deleteByAssignment(assignment);
            for (MultipartFile file : files) {
                Attachment attachment = attachmentService.save(file, user);
                AssignmentAttach assignmentAttach = new AssignmentAttach();
                assignmentAttach.setAssignment(assignment);
                assignmentAttach.setAttachment(attachment);
                assignmentAttachRepository.save(assignmentAttach);
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
