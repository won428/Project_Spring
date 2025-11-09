package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.repository.GradeRepository;
import com.secondproject.secondproject.service.GradeSaveRequest;
import com.secondproject.secondproject.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class GradeController {

    private final GradeRepository gradeRepository;
    private final GradeService gradeService;

    // 성적 점수 저장하기
    @PostMapping("/insertGrades")
    public ResponseEntity<Map<String, Object>> saveGrade(@Valid @RequestBody GradeSaveRequest req){
        Long id = gradeService.createGrade(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id",id));
    }
}
