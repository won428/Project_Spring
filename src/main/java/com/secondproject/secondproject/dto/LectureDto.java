package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LectureDto {
    private Long id; // 강의코드

    private String name; // 강의명

    private User user;

    private int credit;

    private LocalDate startDate;

    private LocalDate endDate;

    private int totalStudent;

    public LectureDto(Long id, String name, User user, int credit, LocalDate startDate, LocalDate endDate, int totalStudent) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.credit = credit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalStudent = totalStudent;
    }

    public static LectureDto fromEntity(Lecture lecture) {
        LectureDto lectureDto = new LectureDto();
        lectureDto.setId(lecture.getId());
        lectureDto.setName(lecture.getName());
        lectureDto.setCredit(lecture.getCredit());
        lectureDto.setUser(lecture.getUser());
        lectureDto.setStartDate(lecture.getStartDate());
        lectureDto.setTotalStudent(lecture.getTotalStudent());
        return lectureDto;
    }

}
