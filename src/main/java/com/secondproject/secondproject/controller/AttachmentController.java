package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.DownloadFile;
import com.secondproject.secondproject.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attachment")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> LectureFileDownload(@PathVariable Long id){
        DownloadFile downloadFile = this.attachmentService.downloadFile(id);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.parseMediaType(downloadFile.getContentType()));

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(downloadFile.getName(), java.nio.charset.StandardCharsets.UTF_8)
                        .build()
        );

        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, // 헤더에 CORS관련 허용 추가
                "Content-Disposition, Content-Type, Content-Length");

        headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        if (downloadFile.getSha256() != null && !downloadFile.getSha256().isBlank()) {
            headers.setETag("\"" + downloadFile.getSha256() + "\"");
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(downloadFile.getInputStream()));

    }
}
