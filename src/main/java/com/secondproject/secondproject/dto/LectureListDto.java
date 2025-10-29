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
public class LectureListDto {
    private Long id; // 강의코드

    private String name; // 강의명

    private User user;

    private int credit;

    private LocalDate startDate;

    private LocalDate endDate;

    private int totalStudent;

    public LectureListDto(Long id, String name, User user, int credit, LocalDate startDate, LocalDate endDate, int totalStudent) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.credit = credit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalStudent = totalStudent;
    }

    public static LectureListDto fromEntity(Lecture lecture) {
        LectureListDto lectureListDto = new LectureListDto();
        lectureListDto.setId(lecture.getId());
        lectureListDto.setName(lecture.getName());
        lectureListDto.setCredit(lecture.getCredit());
        lectureListDto.setUser(lecture.getUser());
        lectureListDto.setStartDate(lecture.getStartDate());
        lectureListDto.setTotalStudent(lecture.getTotalStudent());
        return lectureListDto;
    }

}
