package com.secondproject.secondproject.controller;


import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.LectureNotice;
import com.secondproject.secondproject.service.AssignmentService;
import com.secondproject.secondproject.service.SubmitAsgmtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/assign")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final SubmitAsgmtService submitAsgmtService;


    @PostMapping("/insert")
    public ResponseEntity<?> uploadAssignment(
            @ModelAttribute AssignmentInsertDto assignmentDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        assignmentService.insertAttachment(assignmentDto, files);
        return ResponseEntity.ok("등록에 성공했습니다.");
    }


    @GetMapping("/List")
    public ResponseEntity<?> AssignmentList(
            @RequestParam Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<AssignmentDto> res = assignmentService.getPagedNotices(id, page, size);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace(); // 에러의 전체 내용을 콘솔(err)에 출력
            return ResponseEntity.badRequest().build();

        }
    }


    @GetMapping("/specific")
    public ResponseEntity<?> SpecPage(@RequestParam Long id, @RequestParam String email) {
        try {
            AssignmentResDto resDto = assignmentService.findById(id, email);
            return ResponseEntity.ok(resDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/submit")
    public ResponseEntity<?> AssignSubmit(
            @ModelAttribute AssignSubmitInsertDto assignSubmitInsertDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            submitAsgmtService.assignmentSubmit(assignSubmitInsertDto, files);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/update/{assignId}")
    public ResponseEntity<?> AssignUpdate(
            @PathVariable Long assignId,
            @ModelAttribute AssignSubmitInsertDto assignSubmitInsertDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        try {
            submitAsgmtService.assignmentUpdate(assignSubmitInsertDto, files);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }


    }

    @PutMapping("/assignupdate/{assignId}")
    public ResponseEntity<?> AssignNoticeUpdate(
            @PathVariable Long assignId,
            @ModelAttribute AssignSubmitInsertDto assignSubmitInsertDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        try {
            assignmentService.updateAssignment(assignId, assignSubmitInsertDto, files);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }


    }

}
