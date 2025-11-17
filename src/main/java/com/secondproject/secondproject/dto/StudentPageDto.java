package com.secondproject.secondproject.dto;

import java.util.List;

public record StudentPageDto(
        List<EnrollmentStudentDto> enrollmentStudentDtos,
        PageDto pageDto
) {
}
