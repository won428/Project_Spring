package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {
    private final MajorService majorService;
    private final LectureService lectureService;

    // 관리자용 강의 등록
    @PostMapping("/admin/lectureRegister")
    public ResponseEntity<?> lectureRegisterByAdmin(@RequestBody LectureDto lectureDto){

        this.lectureService.insertByAdmin(lectureDto);

        return ResponseEntity.ok(200);
    }

    // 강의 목록
    @GetMapping("/list")
    public List<LectureDto> lectureList(){
        List<LectureDto> lectureDtoList = this.lectureService.findAll();

        return lectureDtoList;
    }

}
