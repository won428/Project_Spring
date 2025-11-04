package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.OnlineLectureDto;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.service.OnlineLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/online")
@RequiredArgsConstructor
public class OnlineLectureController {

    private final OnlineLectureService onlineLectureService;

    @PostMapping("/insert")
    public ResponseEntity<?> createOnlineLec(
            @ModelAttribute OnlineLectureDto onlineLectureDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            onlineLectureService.createOnLec(onlineLectureDto, files);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();

        }

    }

}
