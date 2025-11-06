package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.repository.GradeRepository;
import com.secondproject.secondproject.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class GradeController {

    private final GradeRepository gradeRepository;
    private final GradeService gradeService;

    @PostMapping("/insertGrades")
    public ResponseEntity<?> saveGrade(){
 return null;
    }
}
