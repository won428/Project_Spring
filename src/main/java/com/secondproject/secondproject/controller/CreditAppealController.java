package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.service.CreditAppealService;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class CreditAppealController {

    private final CreditAppealService appealService;
    private final LectureService lectureService;
    private final UserService userService;

    // GET /api/enrollments?userId=1
    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentInfoDto>> getEnrollments(@RequestParam Long userId) {
        List<EnrollmentInfoDto> enrollments = appealService.findEnrollmentsByUserId(userId);
        return ResponseEntity.ok(enrollments);
    }

    // POST /api/appeals
    @PostMapping("/myappeal")
    public ResponseEntity<?> createAppeal(@RequestBody GradeAppealDto appealForm) {

        this.appealService.createGradeAppeal(appealForm);


        return ResponseEntity.ok(200);
    }

    // 교수 이름 찾아오기
    @GetMapping("/users/{id}")
    public ResponseEntity<UserNameDto> getUserName(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserNameById(id));
    }

    @GetMapping("/lectures/{lectureId}")
    public ResponseEntity<LectureBasicInfoDto> getLectureBasicInfo(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLectureBasicInfo(lectureId));
    }

    @GetMapping("/mylist")
    public ResponseEntity<List<AppealListDto>> getMyAppeals(@RequestParam Long id) {
        return ResponseEntity.ok(appealService.getAppealsByStudentId(id));
    }
}


// 교수로 로그인 시 성적 이의신청 처리 매핑 필요