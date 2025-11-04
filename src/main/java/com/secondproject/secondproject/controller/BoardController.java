package com.secondproject.secondproject.controller;


import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.service.BoardService;
import com.secondproject.secondproject.service.LectureNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/Entire")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/insert")
    public ResponseEntity<?> createNotice(
            @ModelAttribute BoardDto boardDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {

            boardService.createNotice(boardDto, files);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/List")
    public ResponseEntity<?> NotionList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {
        try {
            Page<BoardListDto> res = boardService.getPagedNotices(page, size);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace(); // 에러의 전체 내용을 콘솔(err)에 출력
            return ResponseEntity.badRequest().build();

        }
    }


    @GetMapping("/Specific")
    public ResponseEntity<?> NoticeSpec(@RequestParam Long id) {
        try {
//           게시물 id 로 불러오고
            BoardResponseDto dto = boardService.findById(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{noticeId}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long id) {
        try {
            boardService.deleteNotice(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNotice(
            @PathVariable Long id,
            @ModelAttribute BoardListDto boardListDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "existingFileKeys", required = false) List<String> existingFileKeys) {
        try {

            boardService.updateNotice(id, boardListDto, files, existingFileKeys);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }


    }
}
