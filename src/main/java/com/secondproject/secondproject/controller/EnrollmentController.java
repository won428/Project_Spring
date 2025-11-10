package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.EnrollmentRequestDto;
import com.secondproject.secondproject.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @GetMapping("/selectAll")
    public List<EnrollmentRequestDto> selectAllLectures(Long userId){
        return enrollmentService.selectAllLectures(userId);
    }
}
