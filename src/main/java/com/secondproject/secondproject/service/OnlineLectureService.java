package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.OnlineLectureDto;
import com.secondproject.secondproject.dto.OnlineLectureResDto;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.Mapping.BoardAttach;
import com.secondproject.secondproject.entity.Mapping.OnlineLectureAttach;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.OnlineLectureAttachRepository;
import com.secondproject.secondproject.repository.OnlineLectureRepository;
import com.secondproject.secondproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnlineLectureService {
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final OnlineLectureRepository onlineLectureRepository;
    private final OnlineLectureAttachRepository onlineLectureAttachRepository;


    public void createOnLec(OnlineLectureDto dto, List<MultipartFile> files) throws IOException {
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


        List<MultipartFile> safeFiles = (files == null)
                ? java.util.Collections.emptyList()
                : files.stream().filter(f -> f != null && !f.isEmpty()).toList();

        if (safeFiles.isEmpty()) {
            return;
        }

        for (MultipartFile file : files) {
            Attachment attachment = attachmentService.saveVid(file, user);
            OnlineLectureAttach boardAttach = new OnlineLectureAttach();
            boardAttach.setAttachment(attachment);
            lecture.setPath(attachment.getStoredKey());
            OnlineLecture saved = onlineLectureRepository.save(lecture);
            boardAttach.setOnlineLecture(saved);
            onlineLectureAttachRepository.save(boardAttach);
        }


    }

    public Page<OnlineLectureDto> getPage(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by((Sort.Direction.DESC), "endDate"));
        Page<OnlineLecture> lectures = onlineLectureRepository.findByLectureId(id, pageable);
        return lectures.map(OnlineLectureDto::fromEntity);


    }

    public OnlineLectureResDto findById(Long id) {
        OnlineLecture onlineLecture = onlineLectureRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("없음"));


        List<OnlineLectureAttach> onlineLectureAttaches = onlineLectureAttachRepository.findByOnlineLecture(onlineLecture);


        List<Attachment> attachments = onlineLectureAttaches.stream()
                .map(OnlineLectureAttach::getAttachment)
                .toList();

        return OnlineLectureResDto.fromEntity(onlineLecture, attachments);

    }
}
