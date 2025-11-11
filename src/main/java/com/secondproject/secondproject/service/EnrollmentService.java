package com.secondproject.secondproject.service;

import com.secondproject.secondproject.repository.EnrollmentView;
import com.secondproject.secondproject.repository.EnrollmentRepository;
import com.secondproject.secondproject.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;

    public List<EnrollmentView> selectAllLectures(Long userId) {
        return enrollmentRepository.findDtoByUserId(userId);
    }
}
