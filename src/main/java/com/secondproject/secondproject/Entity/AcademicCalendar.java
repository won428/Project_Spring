package com.secondproject.secondproject.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "academic_calendar")
public class AcademicCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "calendar_id")
    private Long calendarId;

    @Column(name = "cal_start_date", nullable = false)
    private LocalDate calStartDate;

    @Column(name = "cal_end_date",nullable = false)
    private  LocalDate calEndDate;

    @Column(name = "cal_title",length = 50, nullable = false)
    private String calTitle;

    @Column(name = "cal_description",length = 255)
    private String calDescription;


}
