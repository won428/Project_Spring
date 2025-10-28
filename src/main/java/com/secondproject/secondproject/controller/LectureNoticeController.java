package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.LectureNoticeListDto;
import com.secondproject.secondproject.dto.LectureNoticeListWithFileDto;
import com.secondproject.secondproject.dto.LectureNoticeUploadDto;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.service.LectureNoticeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class LectureNoticeController {
    private final LectureNoticeService lectureNoticeService;

    @PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> NoticeUpload(
            @ModelAttribute LectureNoticeUploadDto noticeDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            lectureNoticeService.createNotice(noticeDto, files);
            return ResponseEntity.ok("등록에 성공했습니다.");
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/List")
    public ResponseEntity<List<LectureNoticeListDto>> NotionList(@RequestParam String email) {

        try {
            List<LectureNoticeListDto> noticeList = lectureNoticeService.getNoticeByEmail(email);

            return ResponseEntity.ok(noticeList);
        } catch (Exception e) {
            e.printStackTrace(); // 에러의 전체 내용을 콘솔(err)에 출력
            return ResponseEntity.badRequest().build();

        }
    }

    @GetMapping("/specific")
    public ResponseEntity<?> SpecPage(@RequestParam Long id, @RequestParam String email) {
        try {
//           게시물 id 로 불러오고
            LectureNoticeListWithFileDto attachments = lectureNoticeService.findById(id);


            return ResponseEntity.ok(attachments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

}
