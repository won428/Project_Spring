package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.AssignmentAttach;
import com.secondproject.secondproject.entity.Mapping.NoticeAttach;
import com.secondproject.secondproject.repository.AssignmentAttachRepository;
import com.secondproject.secondproject.repository.AssignmentRepository;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.UserRepository;
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


    public Page<AssignmentDto> getPagedNotices(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by((Sort.Direction.DESC), "createAt"));
        Page<Assignment> result = assignmentRepository.findByUser(user, pageable);
        return result.map(AssignmentDto::fromEntity);
    }

    public AssignmentResDto findById(Long id) {
        List<AssignmentAttach> assignmentAttaches = assignmentAttachRepository.findByAssignment_Id(id);

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("과제 없다."));
        List<Attachment> attachments = assignmentAttaches.stream()
                .map(AssignmentAttach::getAttachment)
                .toList();

        return AssignmentResDto.fromEntity(assignment, attachments);
    }
}
