package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.CompletionDiv;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.entity.GradingWeights;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.LectureSchedule;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureDto {

    private Long id; // 강의코드

    private String name; // 강의명

    private Long user; // 유저(교수)아이디가 들어갑니다

    private String userName; // 교수이름

    private int credit; // 몇 학점인지 표기합니다.(2학점, 3학점)

    private LocalDate startDate; // 강의 시작일

    private LocalDate endDate; // 강의 종강일

    private String description; // 강의를 등록할때 작성하는 강의 한 줄 소개 입니다.

    private Long major; // 어느 학과 강의인지 표기합니다.

    private String majorName; // 학과 이름

    private int totalStudent; // 정원이 몇명인지 표기합니다. 정원을 초과하여 유저(학생이) 강의를 수강 할 수 없습니다.

    private Long nowStudent;

    //강의 상태 : 대기중, 개설, 폐강등 ENUM에 필요한 상수 추가해주세요.
    @Enumerated(EnumType.STRING)
    private Status status;

    private int level;

    @Enumerated(EnumType.STRING)
    private CompletionDiv completionDiv;

    @Enumerated(EnumType.STRING)
    private Status lecStatus;

    private List<LectureScheduleDto> lectureSchedules;

    private List<AttachmentDto> attachmentDtos;

    private Long college;

    private GradingWeightsDto weightsDto;



    public static LectureDto fromEntity(Lecture lecture) {
        LectureDto lectureListDto = new LectureDto();
        lectureListDto.setId(lecture.getId());
        lectureListDto.setName(lecture.getName());
        lectureListDto.setCredit(lecture.getCredit());
        lectureListDto.setUser(lecture.getUser().getId());
        lectureListDto.setUserName(lecture.getUser().getName());
        lectureListDto.setStartDate(lecture.getStartDate());
        lectureListDto.setTotalStudent(lecture.getTotalStudent());
        return lectureListDto;
    }

}
