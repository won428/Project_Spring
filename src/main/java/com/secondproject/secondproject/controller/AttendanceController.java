package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.AttendanceResponseDto;
import com.secondproject.secondproject.repository.AttendanceRecordsRepository;
import com.secondproject.secondproject.repository.EnrollmentRepository;
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
    private final EnrollmentRepository enrollmentRepository;

    // 출결점수 불러오기
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

    // 학생의 출결점수 조회
    @GetMapping("/selectAllAttendance/{enrollmentId}")
    public ResponseEntity<List<AttendanceResponseDto>> listAttendance(@RequestParam Long userId,@PathVariable Long enrollmentId){
        List<AttendanceResponseDto> dtoList = attendanceStudentService.listAttendance(userId, enrollmentId);
        return ResponseEntity.ok(dtoList);
    }
}
