package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.BoardListDto;
import com.secondproject.secondproject.dto.BoardUpNoticeDto;
import com.secondproject.secondproject.entity.Notice;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.BoardRepository;
import com.secondproject.secondproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    @Transactional // 데이터 변경이 있으므로 트랜잭션 처리
    public void createNotice(BoardUpNoticeDto noticeDto) {
        if (noticeDto.getEmail() == null || noticeDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("사용자 이메일이 없습니다.");
        }

        // 1. DTO에서 이메일을 가져와 사용자를 찾습니다.
        User user = userRepository.findByEmail(noticeDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));

        // 2. Notice 객체를 생성하고 값을 설정합니다.
        Notice notice = new Notice();
        notice.setUser(user);
        notice.setTitle(noticeDto.getTitle());
        notice.setContent(noticeDto.getContent());

        // 3. 생성된 Notice를 저장합니다.
        boardRepository.save(notice);
    }


    @Transactional
    public List<BoardListDto> getNoticeById(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));


        List<BoardListDto> noticeListDto = new ArrayList<>();
        List<Notice> noticeList = boardRepository.findNoticeByUser(user);

        for (Notice notice : noticeList) {
            noticeListDto.add(BoardListDto.fromEntity(notice));
        }


        return noticeListDto;
    }
}
