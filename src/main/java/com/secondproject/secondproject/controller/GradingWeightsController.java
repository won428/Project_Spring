package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.repository.GradingWeightsRepository;
import com.secondproject.secondproject.service.GradingWeightsDto;
import com.secondproject.secondproject.service.GradingWeightsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/gradingWeights")
@RequiredArgsConstructor
public class GradingWeightsController {

    private final GradingWeightsRepository gradingWeightsRepository;
    private final GradingWeightsService gradingWeightsService;

    @GetMapping("/selectAll/{lectureId}")
    public ResponseEntity<GradingWeightsDto> selectAllGraidingWeights(@PathVariable Long lectureId){
        GradingWeightsDto weightsDto = gradingWeightsService.findById(lectureId);
        return ResponseEntity.ok(weightsDto);
    }
}
