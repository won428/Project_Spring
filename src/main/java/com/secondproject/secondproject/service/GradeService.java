package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.GradeResponseDto;
import com.secondproject.secondproject.dto.GradingInputs;
import com.secondproject.secondproject.dto.GradingResult;
import com.secondproject.secondproject.entity.Grade;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final GradingCalculator gradingCalculator;
    private final GradingWeightsRepository gradingWeightsRepository;

    // 클라이언트에서 받아온 payload 저장하기
    public Long createGrade(@Valid GradeSaveRequest req) {
        Grade grade = gradeRepository.findByUser_IdAndLecture_Id(req.userId(), req.lectureId());
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"해당하는 학생이 없습니다."));
        Lecture lec = lectureRepository.findById(req.lectureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"해당하는 강의가 없습니다."));
        // 강의 ID로 점수 가중치 가져오기
        var w = gradingWeightsRepository.findByLecture_Id(req.lectureId());

        // gradingCalculator에 넣을 GradingWeightsDto(점수 가중치 생성)
        var dto = new GradingWeightsDto(
                w.getAttendanceScore(),
                w.getAssignmentScore(),
                w.getMidtermExam(),
                w.getFinalExam());

        // 계산할 점수
        GradingInputs in = new GradingInputs(
                req.attendance(),
                req.asScore(),
                req.tScore(),
                req.ftScore());

        // GradingCalculator로 총점, 학점 계산
        GradingResult res = gradingCalculator.gradeCalculating(in,dto);

        grade.setUser(user);
        grade.setLecture(lec);
        grade.setAScore(req.attendance());
        grade.setAsScore(req.asScore());
        grade.setTScore(req.tScore());
        grade.setFtScore(req.ftScore());
        grade.setTotalScore(res.totalScore());
        grade.setLectureGrade(res.gpa());

        gradeRepository.save(grade);
        return grade.getId();
    }

    public List<GradeSummaryDto> existsByGrade(Long lectureId) {
        return gradeRepository.findSummariesByLectureId(lectureId);
    }


}
