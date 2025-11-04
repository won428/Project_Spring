package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.AppealListDto;
import com.secondproject.secondproject.dto.CreditAppealDto;
import com.secondproject.secondproject.dto.EnrollmentInfoDto;
import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.service.CreditAppealService;
import com.secondproject.secondproject.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class CreditAppealController {

    private final CreditAppealService appealService;
    private final LectureService lectureService;

    // GET /api/enrollments?userId=1
    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentInfoDto>> getEnrollments(@RequestParam Long userId){
        List<EnrollmentInfoDto> enrollments = appealService.findEnrollmentsByUserId(userId);
        return ResponseEntity.ok(enrollments);
    }

    // POST /api/appeals
    @PostMapping("/myappeal")
    public ResponseEntity<?> createAppeal(@RequestBody CreditAppealDto dto){
        CreditAppealDto saved = new CreditAppealDto();
        LectureDto lectureDto = this.lectureService.findByID(dto.getLectureId());
        System.out.println(dto);

        saved.setStatus(dto.getStatus());
        saved.setAppealType(dto.getAppealType());
        saved.setContent(dto.getContent());
        saved.setAppealDate(dto.getAppealDate());
        saved.setSendingId(dto.getSendingId());
        saved.setLectureId(dto.getLectureId());
        saved.setReceiverId(dto.getReceiverId());
        saved.setTitle(dto.getTitle());

        this.appealService.createAppeal(saved);

        return ResponseEntity.ok(saved);
    }

//    @GetMapping("/my")
//    public List<AppealListDto> getMyAppeals(@RequestParam("id") String userId) {
//        return appealService.getMyAppeals(userId);
//    }
}

// 교수로 로그인 시 성적 이의신청 처리 매핑 필요