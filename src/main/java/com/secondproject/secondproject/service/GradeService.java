package com.secondproject.secondproject.service;

import com.secondproject.secondproject.entity.Grade;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.GradeRepository;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;

    // 클라이언트에서 받아온 payload 저장하기
    public Long createGrade(@Valid GradeSaveRequest req) {
        Grade grade = new Grade();
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"해당하는 학생이 없습니다."));
        Lecture lec = lectureRepository.findById(req.lectureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"해당하는 강의가 없습니다."));

        grade.setUser(user);
        grade.setLecture(lec);
        grade.setAScore(req.attendance());
        grade.setAsScore(req.asScore());
        grade.setTScore(req.tScore());
        grade.setFtScore(req.ftScore());
        grade.setTotalScore(req.totalScore());
        grade.setLectureGrade(req.gpa());

        gradeRepository.save(grade);
        return grade.getId();
    }
}
