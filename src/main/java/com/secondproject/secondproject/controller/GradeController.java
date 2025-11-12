package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.GradeResponseDto;
import com.secondproject.secondproject.dto.GradingResult;
import com.secondproject.secondproject.repository.GradeRepository;
import com.secondproject.secondproject.repository.GradingWeightsRepository;
import com.secondproject.secondproject.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
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

    // 성적이 저장된 학생 검증
    @GetMapping("/listByGrade")
    public List<GradeSummaryDto> listByGrade(@RequestParam Long lectureId){
        return gradeService.existsByGrade(lectureId);
    }

}
