package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.AcademicCalendar;
import com.secondproject.secondproject.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AcademicCalendarDto {
    private Long id;

    private LocalDate calStartDate;

    private LocalDate eneDate;

    private String title;

    private String description;


    public static AcademicCalendarDto fromEntity(AcademicCalendar academicCalendar) {
        AcademicCalendarDto academicCalendarDto = new AcademicCalendarDto();
        academicCalendarDto.setId(academicCalendarDto.getId());
        academicCalendarDto.setTitle(academicCalendar.getTitle());
        academicCalendarDto.setDescription(academicCalendarDto.getDescription());
        academicCalendarDto.setCalStartDate(academicCalendarDto.getCalStartDate());
        academicCalendarDto.setEneDate(academicCalendar.getEneDate());

        return academicCalendarDto;
    }


}
