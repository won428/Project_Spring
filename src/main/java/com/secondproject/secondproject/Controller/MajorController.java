package com.secondproject.secondproject.Controller;


import com.secondproject.secondproject.Dto.MajorInCollegeDto;
import com.secondproject.secondproject.Entity.Major;
import com.secondproject.secondproject.Service.MajorService;
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

    @GetMapping("/list")
    public List<MajorInCollegeDto> majorListByCollege(@RequestParam("college_id") Long collegeId){
        List<MajorInCollegeDto> majorList = majorService.getMajorListByCollege(collegeId);
        return majorList;
    }
}

