package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.SubmitStatus;
import com.secondproject.secondproject.dto.AssignSubmitInsertDto;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.NoticeAttach;
import com.secondproject.secondproject.entity.Mapping.SubmitAssignAttach;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.exception.NullArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmitAsgmtService {

    private final SubmitAsgmtRepository submitAsgmtRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final AssignmentRepository assignmentRepository;
    private final AttachmentService attachmentService;
    private final SubmitAssignAttachRepository submitAssignAttachRepository;

    @Transactional
    public void assignmentSubmit(
            AssignSubmitInsertDto insertDto,
            List<MultipartFile> files) throws IOException {
        Long userCode = Long.parseLong(insertDto.getUsername());
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없다"));
//        Lecture lecture = lectureRepository.findById(insertDto.getLectureId())
//                .orElseThrow(() -> new EntityNotFoundException("강의 없음."));

        Assignment assignment = assignmentRepository.findById(insertDto.getAssignId())
                .orElseThrow(() -> new EntityNotFoundException("자료 없음."));

        if (LocalDate.now().isAfter(assignment.getDueAt())) {
            throw new BusinessLogicException("제출 기한이 지났습니다.");
        }//마감일이 지났는가?

        SubmitAsgmt submitAsgmt = new SubmitAsgmt();
        submitAsgmt.setUser(user);
        submitAsgmt.setTitle(insertDto.getTitle());
        submitAsgmt.setContent(insertDto.getContent());
        submitAsgmt.setAssignment(assignment);
        submitAsgmt.setSubmitStatus(SubmitStatus.COMPLETED);


        SubmitAsgmt submitAsgmtUp = submitAsgmtRepository.save(submitAsgmt);
        List<MultipartFile> safeFiles = (files == null)
                ? java.util.Collections.emptyList()
                : files.stream().filter(f -> f != null && !f.isEmpty()).toList();

        if (safeFiles.isEmpty()) {
            return;
        }
        for (MultipartFile file : files) {
            Attachment attachment = attachmentService.save(file, user);
            SubmitAssignAttach saved = new SubmitAssignAttach();
            saved.setSubmitAsgmt(submitAsgmtUp);
            saved.setAssignment(assignment);
            saved.setAttachment(attachment);
            submitAssignAttachRepository.save(saved);

        }
    }

    @Transactional
    public void assignmentUpdate(Long assignId, AssignSubmitInsertDto assignSubmitInsertDto, List<MultipartFile> files, List<String> existingFileKeys) throws IOException {
        Long userCode = Long.parseLong(assignSubmitInsertDto.getUsername());
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new EntityNotFoundException("유저 정보 없음"));
        Assignment assignment = assignmentRepository.findById(assignId)
                .orElseThrow(() -> new EntityNotFoundException("과제가 존재하지 않는다."));

        List<SubmitAsgmt> submitAsgmt = submitAsgmtRepository.findByAssignmentId(assignId);

        SubmitAsgmt submitAsgmtOne = submitAsgmt.stream()
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("sd"));


        submitAsgmtOne.setTitle(assignSubmitInsertDto.getTitle());
        submitAsgmtOne.setContent(assignSubmitInsertDto.getContent());
        submitAsgmtRepository.save(submitAsgmtOne);

        List<SubmitAssignAttach> noticeAttach = submitAssignAttachRepository.findBySubmitAsgmt(submitAsgmtOne);
        if (noticeAttach != null && !noticeAttach.isEmpty()) {
            submitAssignAttachRepository.deleteAll(noticeAttach);
        }

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Attachment attachment = attachmentService.save(file, submitAsgmtOne.getUser());
                SubmitAssignAttach saved = new SubmitAssignAttach();
                saved.setSubmitAsgmt(submitAsgmtOne);
                saved.setAssignment(assignment);
                saved.setAttachment(attachment);
                submitAssignAttachRepository.save(saved);
            }
        }
        if ((existingFileKeys != null && !existingFileKeys.isEmpty())) {
            for (String storedKey : existingFileKeys) {
                Attachment existingAttachment = attachmentService.findByStoredKey(storedKey)
                        .orElseThrow(() -> new EntityNotFoundException("기존 파일 키를 찾을 수 없습니다: " + storedKey));
                SubmitAssignAttach reSaved = new SubmitAssignAttach();
                reSaved.setSubmitAsgmt(submitAsgmtOne);
                reSaved.setAssignment(assignment);
                reSaved.setAttachment(existingAttachment);
                submitAssignAttachRepository.save(reSaved);
            }

        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request를 기본값으로 설정
    public class BusinessLogicException extends RuntimeException {

        public BusinessLogicException(String message) {
            super(message); // Controller가 사용할 오류 메시지
        }

    }


}
