package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Gender;
import com.secondproject.secondproject.Enum.Student_status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailAllDto {
    private Long id;

    private String email;

    private Long userCode;

    private String name;

    @Enumerated(EnumType.STRING)
    private Student_status status;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phone;

    private MajorResponseDto major;

    private CollegeResponseDto college;

    private int level = 1; // 학년

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate admissionDate; // 입학일

    private int  totalCredit = 0;

    private int  majorCredit = 0;

    private int  generalCredit = 0;

    private BigDecimal lectureGrade; // 학점

    List<StudentRecordDto> studentRecordList;

    List<GradeForStuInfoDto> gradeInfoList;


}
