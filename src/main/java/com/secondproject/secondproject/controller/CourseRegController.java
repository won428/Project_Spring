package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.entity.CourseRegistration;
import com.secondproject.secondproject.service.CourseRegService;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courseReg")
@RequiredArgsConstructor
public class CourseRegController {

    private final CourseRegService courseRegService;


    // 장바구니 확정 버튼
    @PutMapping("/applyStatus")
    public ResponseEntity<?> applyStatus(@RequestParam Long lecId,@RequestParam Long id, @RequestParam("status") Status status){
        if (id == null || id < 0 || status == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "존재하지 않는 강의이거나 존재하지 않는 상태 표시입니다."));
        }

        this.courseRegService.updateStatus(lecId, id, status);

        return ResponseEntity.ok(200);
    }

    @PatchMapping("/statusAll")
    public ResponseEntity<?> statusAll(@RequestParam(name = "selected", required = false) List<Long> selected,
                                       @RequestParam Long id,
                                       @RequestParam("status") Status status){
        try {
            this.courseRegService.statusAll(id, selected, status);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (ResponseStatusException ex) {
            try {

                int errStatus = ex.getStatusCode().value();
                String error = HttpStatus.valueOf(errStatus).name();

                Map<String, Object> body = Map.of(
                        "status", errStatus,
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

    @PatchMapping("/delete")
    public ResponseEntity<?> deleteCourseReg(@RequestParam Long id, @RequestParam Long lecId){
        try {
                this.courseRegService.delete(id, lecId);
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

    @PatchMapping("/delete/all")
    public ResponseEntity<?> deleteCourseRegAll(@RequestParam Long id,
                                                @RequestParam(name = "cancelSelected", required = false) List<Long> cancelSelected){
        try {
            this.courseRegService.deleteAll(id,cancelSelected);
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
