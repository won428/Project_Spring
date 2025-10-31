package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.SubmitStatus;
import com.secondproject.secondproject.dto.AssignSubmitInsertDto;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.SubmitAssignAttach;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public void assignmentSubmit(
            AssignSubmitInsertDto insertDto,
            List<MultipartFile> files) throws IOException {
        User user = userRepository.findByEmail(insertDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없다"));
//        Lecture lecture = lectureRepository.findById(insertDto.getLectureId())
//                .orElseThrow(() -> new EntityNotFoundException("강의 없음."));

        Assignment assignment = assignmentRepository.findById(insertDto.getAssignId())
                .orElseThrow(() -> new EntityNotFoundException("자료 없음."));

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
}
