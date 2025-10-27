package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.BoardListDto;
import com.secondproject.secondproject.dto.BoardUpNoticeDto;
import com.secondproject.secondproject.entity.Notice;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/insert")
    public ResponseEntity<?> NoticeUpload(@RequestBody BoardUpNoticeDto noticeDto) {
        try {
            // 2. 서비스의 createNotice 메서드 호출 하나로 로직 처리 끝
            boardService.createNotice(noticeDto);
            return ResponseEntity.ok("등록에 성공했습니다.");
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            // 3. 서비스에서 발생한 예외를 처리하여 적절한 에러 응답 반환
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/List")
    public ResponseEntity<List<BoardListDto>> NotionList(@RequestParam String email) {
        try {
            List<BoardListDto> noticeList = boardService.getNoticeById(email);

            return ResponseEntity.ok(noticeList);
        } catch (Exception e) {
            e.printStackTrace(); // 에러의 전체 내용을 콘솔(err)에 출력
            return ResponseEntity.badRequest().build();

        }


    }

}
