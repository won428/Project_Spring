package com.secondproject.secondproject.Controller;

import com.secondproject.secondproject.Entity.College;
import com.secondproject.secondproject.Service.CollegeService;
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

        @GetMapping("list")
        public List<College> collegeList(){

            List<College> collegeList = this.collegeService.getCollegeList();
            return collegeList;
        }
}
