package com.secondproject.secondproject.controller;


import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.service.AssignmentService;
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
    public ResponseEntity<?> SpecPage(@RequestParam Long id) {
        try {
            AssignmentResDto resDto = assignmentService.findById(id);
            return ResponseEntity.ok(resDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }
}
