package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AttachmentVidController {

    @Value("${lectureVid.upload-dir}")
    private String vidDir;

    private final AttachmentRepository attachmentRepository;


    @GetMapping("/vid/{storedKey}")
    @ResponseBody
    public ResponseEntity<Resource> streamVid(@PathVariable String storedKey) {
        Optional<Attachment> attachmentOpt = attachmentRepository.findByStoredKey(storedKey);

        if (!attachmentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Attachment attachment = attachmentOpt.get();
        String contentType = attachment.getContentType();
        String originalFilename = attachment.getName();
        try {
            Path videoPath = Paths.get(vidDir).resolve(storedKey);
            Resource resource = new UrlResource(videoPath.toUri());

            if (resource.exists() && resource.isReadable()) {
                long contentLength = resource.contentLength();
                String contentDisposition = "inline; filename=\"" + originalFilename + "\"";
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }


}
