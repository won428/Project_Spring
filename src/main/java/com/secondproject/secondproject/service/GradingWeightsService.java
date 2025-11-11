package com.secondproject.secondproject.service;

import com.secondproject.secondproject.repository.GradingWeightsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GradingWeightsService {
    private final GradingWeightsRepository gradingWeightsRepository;

    public GradingWeightsDto findById(Long lectureId) {
        return gradingWeightsRepository.findByLectureId(lectureId).map(g -> new GradingWeightsDto(
                g.getAttendanceScore(),
                g.getAssignmentScore(),
                g.getMidtermExam(),
                g.getFinalExam())).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"해당하는 강의가 없습니다."));
    }
}
