package com.secondproject.secondproject.dto;

public record EnrollmentStudentDto(
        Long id,
        Long userCode,
        String name,
        String majorName,
        String email,
        String phone
) {
}