package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.LectureNoticeListDto;
import com.secondproject.secondproject.dto.AttachmentDto;
import com.secondproject.secondproject.dto.LectureNoticeUploadDto;
import com.secondproject.secondproject.dto.NoticeResponseDto;
import com.secondproject.secondproject.service.LectureNoticeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class LectureNoticeController {
    private final LectureNoticeService lectureNoticeService;

    @Value("${file.upload-dir}")
    private String uploadDir;

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
    public ResponseEntity<?> NotionList(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {
        try {
            //   List<LectureNoticeListDto> noticeList = lectureNoticeService.getNoticeByEmail(email);
            Page<LectureNoticeListDto> res = lectureNoticeService.getPagedNotices(email, page, size);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace(); // 에러의 전체 내용을 콘솔(err)에 출력
            return ResponseEntity.badRequest().build();

        }
    }

    @GetMapping("/specific")
    public ResponseEntity<?> SpecPage(@RequestParam Long id) {

        System.out.println("id : " + id);
        try {
//           게시물 id 로 불러오고
            NoticeResponseDto attachments = lectureNoticeService.findById(id);


            return ResponseEntity.ok(attachments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/files/download/{storedKey}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String storedKey) throws IOException {


        Path filePath = Paths.get(uploadDir + storedKey);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("파일이 존재하지 않습니다.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + storedKey.substring(storedKey.indexOf("_") + 1) + "\"").body(resource);
    }

}
