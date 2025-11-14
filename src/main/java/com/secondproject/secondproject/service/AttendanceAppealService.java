package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.AttendanceAppealDto;
import com.secondproject.secondproject.dto.EnrollmentInfoDto;
import com.secondproject.secondproject.dto.LectureBasicInfoDto;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.AppealAttach;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceAppealService {

    private final EnrollmentRepository enrollmentRepository;
    private final AppealRepository appealRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final AppealAttachRepository appealAttachRepository;


    @Value("${file.upload-dir}")
    private String uploadDir;

    // 학생 수강 강의 조회
    @Transactional(readOnly = true)
    public List<EnrollmentInfoDto> findEnrollmentsByUserId(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser_Id(userId);

        return enrollments.stream().map(en -> {
            Lecture lecture = en.getLecture();
            User student = en.getUser();      // enrollment에서 학생 정보
            User professor = lecture.getUser(); // lecture에서 교수 정보

            EnrollmentInfoDto dto = new EnrollmentInfoDto(student, professor, lecture, en);
            return dto;
        }).collect(Collectors.toList());
    }

    // 교수 정보 receiverId로 조회
    public LectureBasicInfoDto findProfessorByLectureId(Long lectureId) {
        Lecture lecture = lectureRepository.findByIdWithProfessor(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        User professor = lecture.getUser(); // 교수 정보 가져오기

        return new LectureBasicInfoDto(
                lecture.getId(),
                lecture.getName(),
                professor.getId(),
                professor.getName() // 교수 이름
        );
    }


    @Transactional
    public Long createAttendanceAppeal(AttendanceAppealDto dto) {
        Appeal appeal = new Appeal();

        // 수강정보 조회
        Enrollment enrollment = enrollmentRepository.findByUserIdAndLectureId(dto.getSendingId(), dto.getLectureId());
        if (enrollment == null) {
            throw new RuntimeException("해당 유저의 수강 정보가 없습니다.");
        }
        appeal.setEnrollment(enrollment);

        // 강의 조회
        Lecture lecture = lectureRepository.findById(dto.getLectureId())
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
        appeal.setLecture(lecture);

        // DTO 값 설정
        appeal.setSendingId(dto.getSendingId());
        appeal.setReceiverId(dto.getReceiverId());
        appeal.setAppealType(dto.getAppealType());
        appeal.setTitle(dto.getTitle());
        appeal.setContent(dto.getContent());
        appeal.setStatus(dto.getStatus());
        appeal.setAppealDate(dto.getAppealDate());
        appeal.setLecture(lecture);
        appeal.setEnrollment(enrollment);
        appeal.setStatus(Status.PENDING);

        // Appeal 저장 후 ID 반환
        appealRepository.save(appeal);
        return appeal.getId();
    }

    @Transactional
    public void mapAttachmentsToAttendanceAppeal(Long appealId, List<Attachment> attachments) {
        Appeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new RuntimeException("Appeal not found"));

        for (Attachment file : attachments) {
            AppealAttach attachMapping = new AppealAttach();
            attachMapping.setAppeal(appeal);
            attachMapping.setAttachment(file);

            appealAttachRepository.save(attachMapping); // ⚡ AttachmentRepository 대신 AppealAttachRepository라면 그쪽으로 변경
        }
    }
}

