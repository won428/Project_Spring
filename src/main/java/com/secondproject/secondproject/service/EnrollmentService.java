package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.EnrollmentRequestDto;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public List<EnrollmentRequestDto> selectAllLectures(Long userId) {
        List<EnrollmentRequestDto> dtoList = enrollmentRepository.findDtoByUserId(userId);

        return dtoList;
    }
}
