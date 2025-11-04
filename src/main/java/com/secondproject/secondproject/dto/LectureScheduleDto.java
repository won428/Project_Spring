package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.LectureSchedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureScheduleDto {

    Long id;

    Lecture lecture;

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    public static LectureScheduleDto fromEntity(LectureSchedule e) {
        return new LectureScheduleDto(
                e.getId(), e.getLecture() != null ? e.getLecture() : null, e.getDay(), e.getStartTime(), e.getEndTime()
        );
    }
}
