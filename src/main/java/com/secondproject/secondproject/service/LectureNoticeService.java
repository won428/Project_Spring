package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.LectureNoticeListDto;
import com.secondproject.secondproject.dto.AttachmentDto;
import com.secondproject.secondproject.dto.LectureNoticeUploadDto;
import com.secondproject.secondproject.dto.NoticeResponseDto;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.LectureNotice;
import com.secondproject.secondproject.entity.Mapping.NoticeAttach;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.BoardRepository;
import com.secondproject.secondproject.repository.LectureNoticeRepository;
import com.secondproject.secondproject.repository.NoticeAttachRepository;
import com.secondproject.secondproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureNoticeService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final NoticeAttachRepository noticeAttachRepository;
    private final LectureNoticeRepository lectureNoticeRepository;


    @Transactional // 데이터 변경이 있으므로 트랜잭션 처리
    public void createNotice(LectureNoticeUploadDto noticeDto, List<MultipartFile> files) throws IOException {
        if (noticeDto.getEmail() == null || noticeDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("사용자 이메일이 없습니다.");
        }

        // 1. DTO 에서 이메일을 가져와 사용자를 찾습니다.
        User user = userRepository.findByEmail(noticeDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));

        // 2. Notice 객체를 생성하고 값을 설정합니다.
        LectureNotice LN = new LectureNotice();
        LN.setUser(user);
        LN.setLnTitle(noticeDto.getTitle());
        LN.setLnContent(noticeDto.getContent());

        // 3. 생성된 Notice를 저장합니다.
        LectureNotice saved = lectureNoticeRepository.save(LN);

        if (!files.isEmpty() && files != null) {

            for (MultipartFile file : files) {
                Attachment attachment = attachmentService.save(file, user);
                NoticeAttach noticeAttach = new NoticeAttach();
                noticeAttach.setAttachment(attachment);
                noticeAttach.setLectureNotice(saved);
                noticeAttachRepository.save(noticeAttach);
            }
        }
    }


    @Transactional
    public List<LectureNoticeListDto> getNoticeByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));

        List<LectureNoticeListDto> noticeListDto = new ArrayList<>();
        List<LectureNotice> noticeList = lectureNoticeRepository.findNoticeByUser(user);


//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formatted = dateTime.format(formatter);

        for (LectureNotice lectureNotice : noticeList) {
            noticeListDto.add(LectureNoticeListDto.fromEntity(lectureNotice));
        }

        return noticeListDto;
    }


    public NoticeResponseDto findById(Long id) {
//        게시물 ID로 attachment 조회 / ListDto 조회 -> Dto에  담아서 Controller 전송
        //

        List<NoticeAttach> noticeAttach = noticeAttachRepository.findByLectureNotice_Id(id);


        //게시물 목록 호출
        LectureNotice lectureNotice = lectureNoticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지 없다"));

        List<Attachment> attachments = noticeAttach.stream()
                .map(NoticeAttach::getAttachment)
                .toList();


        return NoticeResponseDto.fromEntity(lectureNotice, attachments);


//        List<Attachment> NoticesFiles = new ArrayList<>();
//        LectureNoticeListWithFileDto dto = new LectureNoticeListWithFileDto();
//        dto.add(LectureNoticeListWithFileDto.fromEntity(lectureNotice));
//        for (NoticeAttach attachId : noticeAttach) {
//            Attachment attachment = attachmentService.findById(attachId.getAttachment().getId()).orElseThrow(() -> new EntityNotFoundException("ㅅㅄㅄㅂ"));
//            NoticesFiles.add(attachment);
//        }
//        dto.setFiles(NoticesFiles);
//        return dto;
    }
}
