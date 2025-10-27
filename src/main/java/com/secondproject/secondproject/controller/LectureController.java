package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PutMapping("/request")
    public ResponseEntity<?> lectureRequest(@RequestParam Long id, @RequestParam("status") Status status){

        if(id == null || id < 0 || status == null){
            return ResponseEntity.badRequest().body(Map.of("message","존재하지 않는 강의이거나 존재하지 않는 상태 표시입니다."));
        }

        this.lectureService.updateStatus(id,status);

        return ResponseEntity.ok(200);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyLecture(@RequestBody List<Long> idList, @RequestParam Long id){

        this.lectureService.applyLecture(idList, id);

        return ResponseEntity.ok(200);
    }


}
