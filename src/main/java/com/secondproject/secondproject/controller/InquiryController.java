package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.CommentDto;
import com.secondproject.secondproject.dto.InquiryDto;
import com.secondproject.secondproject.service.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {

    private final BoardService boardService;


    @PostMapping("/write")
    public ResponseEntity<?> writeInquiry(
            @RequestPart InquiryDto post,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ){
        try {
            this.boardService.createInquiry(post, files);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (ResponseStatusException ex) {
            try {

                int status = ex.getStatusCode().value();
                String error = HttpStatus.valueOf(status).name();

                Map<String, Object> body = Map.of(
                        "status", status,
                        "error", error,
                        "message", ex.getReason(),
                        "timestamp", java.time.OffsetDateTime.now().toString()
                );

                return ResponseEntity.status(ex.getStatusCode()).body(body);
            } catch (Exception otherEx) {
                return ResponseEntity.status(500).body("알수없는 오류");
            }
        }
    }

    @GetMapping("/myList")
    public List<InquiryDto> myList(@RequestParam Long id){

        List<InquiryDto> inquiryDtoList = this.boardService.myIquiryList(id);

        return inquiryDtoList;
    }

    @GetMapping("/page/{id}")
    public InquiryDto inquiryPage(@PathVariable Long id){
        InquiryDto inquiryDto = this.boardService.getInquiryPage(id);

        return inquiryDto;
    }

    @PatchMapping("/clickTitle")
    public ResponseEntity<?> clickTitle(@RequestParam Long id){
        try {
            this.boardService.plusViewCount(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (ResponseStatusException ex) {
            try {

                int status = ex.getStatusCode().value();
                String error = HttpStatus.valueOf(status).name();

                Map<String, Object> body = Map.of(
                        "status", status,
                        "error", error,
                        "message", ex.getReason(),
                        "timestamp", java.time.OffsetDateTime.now().toString()
                );

                return ResponseEntity.status(ex.getStatusCode()).body(body);
            } catch (Exception otherEx) {
                return ResponseEntity.status(500).body("알수없는 오류");
            }
        }
    }

    @PostMapping("/write/comment")
    public ResponseEntity<?> writeComment(@RequestBody CommentDto comment){
        try {
            this.boardService.writeComment(comment);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (ResponseStatusException ex) {
            try {

                int status = ex.getStatusCode().value();
                String error = HttpStatus.valueOf(status).name();

                Map<String, Object> body = Map.of(
                        "status", status,
                        "error", error,
                        "message", ex.getReason(),
                        "timestamp", java.time.OffsetDateTime.now().toString()
                );

                return ResponseEntity.status(ex.getStatusCode()).body(body);
            } catch (Exception otherEx) {
                return ResponseEntity.status(500).body("알수없는 오류");
            }
        }
    }

}
