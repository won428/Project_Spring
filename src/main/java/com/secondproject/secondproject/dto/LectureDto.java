package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.Enum.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class LectureDto {

    // 강의 코드 & 상태
    // 는 자동삽입

    private String lecName; // 강의명

    private int credit; // 학점(1~4점)

    private LocalDate lec_startDate; // 강의 시작일

    private LocalDate lec_endDate; // 강의 종강일

    private String lec_description; // 강의를 등록할때 작성하는 강의 한 줄 소개 입니다.

    private Long majorId; // 어느 학과 강의인지 표기합니다.

    private int total_student; // 학생 정원

}
