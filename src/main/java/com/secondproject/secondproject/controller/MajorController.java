package com.secondproject.secondproject.controller;


import com.secondproject.secondproject.dto.MajorInCollegeDto;
import com.secondproject.secondproject.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;

    // 단과대학에 속한 학과목록 조회
    @GetMapping("/list") // 매핑주소 수정 필요
    public List<MajorInCollegeDto> majorListByCollege(@RequestParam("college_id") Long collegeId){
        List<MajorInCollegeDto> majorList = majorService.getMajorListByCollege(collegeId);
        return majorList;
    }
}

