package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.StudentCreditDto;
import com.secondproject.secondproject.entity.Grade;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.repository.GradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentCreditService {

    private final GradeRepository gradeRepository;


    // userId 기준 강의(및 학생 성적) 전체 조회 (연도, 학기는 프론트엔드에서 처리)
    public List<StudentCreditDto> findByUserId(Long userId) {
        List<Grade> grades = gradeRepository.findByUserId(userId);

        return grades.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Grade 엔티티 -> DTO 수동 변환
    private StudentCreditDto convertToDto(Grade grade) {
        StudentCreditDto dto = new StudentCreditDto();

        dto.setId(grade.getId());
        if (grade.getUser() != null) {
            dto.setUserId(grade.getUser().getId());
        }
        if (grade.getLecture() != null) {
            Lecture lecture = grade.getLecture();
            lecture.getName(); // 강제로 초기화
            dto.setLectureId(lecture.getId());
            dto.setStartDate(lecture.getStartDate());
            dto.setLecName(lecture.getName());
        }

        dto.setAScore(grade.getAScore());
        dto.setAsScore(grade.getAsScore());
        dto.setTScore(grade.getTScore());
        dto.setFtScore(grade.getFtScore());
        dto.setTotalScore(grade.getTotalScore());
        dto.setLectureGrade(grade.getLectureGrade());

        return dto;
    }
}
