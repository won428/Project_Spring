package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.AcademicCalendarDto;
import com.secondproject.secondproject.entity.AcademicCalendar;
import com.secondproject.secondproject.service.AcademicCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class AcademicCalendarController {

    private final AcademicCalendarService academicCalendarService;

    @GetMapping("/List")
    public ResponseEntity<List<AcademicCalendar>> CalList() {
        try {
            List<AcademicCalendar> calendarDtos = academicCalendarService.getList();

            return ResponseEntity.ok(calendarDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }
}
