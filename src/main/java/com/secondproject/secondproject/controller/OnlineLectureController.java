package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.OnlineLectureDto;
import com.secondproject.secondproject.dto.OnlineLectureResDto;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.service.OnlineLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/List")
    public ResponseEntity<?> OlLECList(
            @RequestParam Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {

            System.out.println("input : " + id + page + size);

            Page<OnlineLectureDto> res = onlineLectureService.getPage(id, page, size);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/Spec")
    public ResponseEntity<?> lecSpec(@RequestParam Long id) {
        try {
            OnlineLectureResDto onlineLectureResDto = onlineLectureService.findById(id);
            return ResponseEntity.ok(onlineLectureResDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

}
