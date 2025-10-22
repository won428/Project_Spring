package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Service.CollegeService;
import com.secondproject.secondproject.entity.College;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/college")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;

    @GetMapping("/list")
    public List<College> collegeList(){
        List<College> collegeList = this.collegeService.collegeList();

        return collegeList;
    }
}
