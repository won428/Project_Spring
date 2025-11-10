package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.AttendStudent;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import com.secondproject.secondproject.dto.OnlineLectureDto;
import com.secondproject.secondproject.dto.OnlineLectureResDto;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.BoardAttach;
import com.secondproject.secondproject.entity.Mapping.OnlineLectureAttach;
import com.secondproject.secondproject.publicMethod.VidLengthGetter;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnlineLectureService {
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final OnlineLectureRepository onlineLectureRepository;
    private final OnlineLectureAttachRepository onlineLectureAttachRepository;
    private final ProgressRepository progressRepository;

    @Transactional
    public void createOnLec(OnlineLectureDto dto, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("유저정보가 존재하지 않습니다."));
        Lecture lectureParent = lectureRepository.findById(dto.getLectureId())
                .orElseThrow(() -> new EntityNotFoundException("강의가 존재하지 않습니다."));
        OnlineLecture lecture = new OnlineLecture();
        lecture.setLecture(lectureParent);
        lecture.setTitle(dto.getTitle());
        lecture.setUsername(user.getName());
        lecture.setUpdatedDate(dto.getStartDate().atTime(0, 0, 0));
        lecture.setEndDate(dto.getEndDate().atTime(23, 59, 0));
        lecture.setDisable(true);
        lecture.setUser(user);


        if (file.isEmpty()) {
            return;
        }

        Attachment attachment = attachmentService.saveVid(file, user);
        OnlineLectureAttach boardAttach = new OnlineLectureAttach();
        boardAttach.setAttachment(attachment);
        int durSec = VidLengthGetter.getDurationInSec(file);
        lecture.setVidLength(durSec);
        lecture.setPath(attachment.getStoredKey());
        OnlineLecture saved = onlineLectureRepository.save(lecture);
        boardAttach.setOnlineLecture(saved);
        onlineLectureAttachRepository.save(boardAttach);


    }

    public Page<OnlineLectureDto> getPage(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by((Sort.Direction.DESC), "endDate"));

        Page<OnlineLecture> lectures = onlineLectureRepository.findByLectureId(id, pageable);
        return lectures.map(OnlineLectureDto::fromEntity);


    }

    public OnlineLectureResDto findById(Long id, Long userid) {
        OnlineLecture onlineLecture = onlineLectureRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없음"));
        User user = userRepository.findById(userid).orElseThrow(() -> new EntityNotFoundException("유저 없음"));
        UserLectureProgress progress = progressRepository
                .findByOnlineLecture(onlineLecture)
                .orElseGet(() ->
                        {
                            UserLectureProgress us = new UserLectureProgress();
                            us.setUser(user);
                            us.setStatus(Status.IN_PROGRESS);
                            us.setAttendanceStatus(AttendStudent.ABSENT);
                            us.setOnlineLecture(onlineLecture);
                            us.setUpdatedAt(LocalDateTime.now());
                            us.setTotalWatchedSec(0);
                            us.setLastViewedSec(0);
                            us.setTotalWatchedSec(0);

                            return progressRepository.save(us);
                        }
                );


        OnlineLectureAttach onlineLectureAttaches = onlineLectureAttachRepository.findByOnlineLecture(onlineLecture);

        Attachment attachment = attachmentService.findById(onlineLectureAttaches.getAttachment().getId())
                .orElseThrow(() -> new EntityNotFoundException("Tlqkf"));

        return OnlineLectureResDto.fromEntity(onlineLecture, attachment, progress);

    }

    public void stackProgress(OnlineLectureResDto dto) {
        OnlineLecture onlineLecture = onlineLectureRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("없는 강좌"));


        UserLectureProgress lec = progressRepository.findByOnlineLecture(onlineLecture)
                .orElseThrow(() -> new EntityNotFoundException("없는 강좌"));
        lec.setLastViewedSec(dto.getLastViewedSec());
        lec.setTotalWatchedSec(dto.getTotalWatchedSec());
        int params = (dto.getTotalWatchedSec() / (dto.getVidLength()));
        if (params > 0.85) {
            lec.setStatus(Status.COMPLETED);
            lec.setAttendanceStatus(AttendStudent.PRESENT);
        }
        progressRepository.save(lec);

    }

    @Transactional
    public void deleteLec(Long id) {
        OnlineLecture onlineLecture = onlineLectureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없음111"));
        OnlineLectureAttach onlineLectureAttach = onlineLectureAttachRepository.findByOnlineLecture(onlineLecture);
        Attachment attachment = attachmentService.findById(onlineLectureAttach.getAttachment().getId()).orElseThrow(() -> new EntityNotFoundException("ddd"));
        UserLectureProgress userLectureProgress = progressRepository.findByOnlineLecture(onlineLecture).orElseThrow(() -> new EntityNotFoundException("없음222"));

        progressRepository.delete(userLectureProgress);
        onlineLectureAttachRepository.delete(onlineLectureAttach);
        attachmentService.deleteById(attachment.getId());
        attachmentService.deleteFile(attachment.getStoredKey());
        onlineLectureRepository.delete(onlineLecture);
    }
}
