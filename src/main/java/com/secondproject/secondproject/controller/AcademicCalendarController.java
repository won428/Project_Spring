package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.AcademicCalendarDto;
import com.secondproject.secondproject.dto.LectureNoticeListDto;
import com.secondproject.secondproject.entity.AcademicCalendar;
import com.secondproject.secondproject.service.AcademicCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/List/ad")
    public ResponseEntity<Page<AcademicCalendar>> CalListAdmin(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<AcademicCalendar> res = academicCalendarService.getListByMonth(year, month, page, size);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCal(
            @PathVariable Long id
    ) {
        try {
            academicCalendarService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
