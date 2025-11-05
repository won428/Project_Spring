package com.secondproject.secondproject.service;


import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.Mapping.BoardAttach;
import com.secondproject.secondproject.entity.Mapping.NoticeAttach;
import com.secondproject.secondproject.entity.Notice;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.*;
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
public class BoardService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final AttachmentService attachmentService;
    private final BoardAttachRepository boardAttachRepository;


    public void createNotice(BoardDto boardDto, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByEmail(boardDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));
        Notice notice = new Notice();
        notice.setUser(user);
        notice.setTitle(boardDto.getTitle());
        notice.setContent(boardDto.getContent());

        Notice saved = boardRepository.save(notice);

        List<MultipartFile> safeFiles = (files == null)
                ? java.util.Collections.emptyList()
                : files.stream().filter(f -> f != null && !f.isEmpty()).toList();

        if (safeFiles.isEmpty()) {
            return;
        }
        for (MultipartFile file : files) {
            Attachment attachment = attachmentService.save(file, user);
            BoardAttach boardAttach = new BoardAttach();
            boardAttach.setAttachment(attachment);
            boardAttach.setNotice(saved);
            boardAttachRepository.save(boardAttach);
        }
    }

    public Page<BoardListDto> getPagedNotices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by((Sort.Direction.DESC), "createdAt"));

        Page<Notice> result = boardRepository.findAll(pageable);
        return result.map(BoardListDto::fromEntity);


    }

    public BoardResponseDto findById(Long id) {

        List<BoardAttach> attaches = boardAttachRepository.findByNoticeId(id);

        Notice notice = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지 존재 X"));

        List<Attachment> attachments = attaches.stream()
                .map(BoardAttach::getAttachment)
                .toList();
        return BoardResponseDto.fromEntity(notice, attachments);


    }

    public void deleteNotice(Long id) {
        Notice notice = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지 없음"));
        List<BoardAttach> boardAttaches = boardAttachRepository.findByNoticeId(id);
        for (BoardAttach bb : boardAttaches) {
            attachmentService.deleteById(bb.getAttachment().getId());
            boardAttachRepository.deleteById(bb.getId());
        }
        boardRepository.deleteById(id);
    }

    public void updateNotice(Long id, BoardListDto boardListDto, List<MultipartFile> files, List<String> existingFileKeys) throws IOException {
        Notice notice = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지 없음"));
        notice.setTitle(boardListDto.getTitle());
        notice.setContent(boardListDto.getContent());
        boardRepository.save(notice);

        List<BoardAttach> boardAttaches = boardAttachRepository.findByNoticeId(notice.getId());
        if (boardAttaches != null && !boardAttaches.isEmpty()) {
            boardAttachRepository.deleteAll(boardAttaches);
        }
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Attachment attachment = attachmentService.save(file, notice.getUser());
                BoardAttach saved = new BoardAttach();
                saved.setNotice(notice);
                saved.setAttachment(attachment);
                boardAttachRepository.save(saved);
            }
        }
        if ((existingFileKeys != null && !existingFileKeys.isEmpty())) {
            for (String storedKey : existingFileKeys) {
                Attachment existingAttachment = attachmentService.findByStoredKey(storedKey)
                        .orElseThrow(() -> new EntityNotFoundException("기존 파일 키를 찾을 수 없습니다: " + storedKey));
                BoardAttach reSaved = new BoardAttach();
                reSaved.setNotice(notice);
                reSaved.setAttachment(existingAttachment);
                boardAttachRepository.save(reSaved);
            }

        }

    }
}
