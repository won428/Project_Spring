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
            @RequestPart(value = "files", required = false) MultipartFile file
    ) {
        try {
            onlineLectureService.createOnLec(onlineLectureDto, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());

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
    public ResponseEntity<?> lecSpec(@RequestParam Long id, @RequestParam Long userId) {
        try {

            System.out.println("강의 id : " + id + "학생 ID : " + userId);
            OnlineLectureResDto onlineLectureResDto = onlineLectureService.findById(id, userId);
            return ResponseEntity.ok(onlineLectureResDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/progress")
    public ResponseEntity<String> progressAdd(@RequestBody OnlineLectureResDto dto) {
        try {
            onlineLectureService.stackProgress(dto);
            return ResponseEntity.ok().body("강좌 시청 진행중 ...");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("오류 발생");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delOLec(@PathVariable Long id) {
        try {
            System.out.println("id :          " + id);
            onlineLectureService.deleteLec(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("오류 발생");
        }

    }
}
