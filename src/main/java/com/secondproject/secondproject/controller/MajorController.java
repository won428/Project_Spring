package com.secondproject.secondproject.controller;


import com.secondproject.secondproject.dto.MajorInCollegeDto;
import com.secondproject.secondproject.dto.MajorInsertDto;
import com.secondproject.secondproject.dto.MajorResponseDto;
import com.secondproject.secondproject.service.CollegeService;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.MajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;
    private final CollegeService collegeService;
    private final LectureService lectureService;

    // 단과대학에 속한 학과목록 조회
    @GetMapping("/list") // 매핑주소 수정 필요
    public List<MajorInCollegeDto> majorListByCollege(@RequestParam("college_id") Long collegeId){
        List<MajorInCollegeDto> majorList = majorService.getMajorListByCollege(collegeId);
        return majorList;
    }

    // 학과 등록
    @PostMapping("/insert")
    public ResponseEntity<MajorResponseDto> insert(@RequestBody @Valid MajorInsertDto majorInsertDto){
        MajorResponseDto responseDto= majorService.insertMajor(majorInsertDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/major/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(null);
    }
}

