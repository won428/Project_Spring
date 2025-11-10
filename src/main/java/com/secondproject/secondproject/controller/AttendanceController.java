package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.AttendanceResponseDto;
import com.secondproject.secondproject.repository.AttendanceRecordsRepository;
import com.secondproject.secondproject.service.AttendanceStudentService;
import com.secondproject.secondproject.service.AttendanceSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceStudentService attendanceStudentService;

    //
    @GetMapping(
            value = "/selectById/{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AttendanceSummary> getAttendanceSummary(
            @PathVariable("id") Long lectureId,
            @RequestParam("userId") Long userId){
        AttendanceSummary body = attendanceStudentService.getAttendanceSummary(lectureId, userId);
        return ResponseEntity.ok(body);
    }
    //
}
