package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.StatusChangeListDto;
import com.secondproject.secondproject.dto.StatusChangeRequestDto;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.service.StatusService;
import com.secondproject.secondproject.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping; // 삭제 요청용
import org.springframework.web.bind.annotation.PathVariable; // 경로 변수 활용
import org.springframework.http.ResponseEntity; // 응답처리

import java.util.List;
import java.util.Map;

import static com.secondproject.secondproject.Enum.UserType.STUDENT;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;
    private final StudentService studentService;

    // 생성: React ChangeStatusPage (POST /api/student/record)
    // 프론트가 body.userId를 포함해 전송하는 방식을 사용
    @PostMapping("/record")
    public ResponseEntity<StatusChangeRequestDto> applyStatusChange(@RequestBody StatusChangeRequestDto dto) {
        StatusChangeRequestDto created = statusService.createChangeRequest(dto);
        return ResponseEntity.ok(created);
    }


//    // 학적변경신청 목록 조회 (쿼리 파라미터 id 사용) 10/29일에 수정
//    @GetMapping("/record/my")
//    public ResponseEntity<?> changeList(@RequestParam("id") Long userId) {
////        // 1) 사용자 존재/타입 검증
////        User user = studentService.getStudentById(userId); // 또는 getStudentById
//
//        // 2) 목록 조회
//        List<StatusChangeListDto> list = statusService.findMyList(userId);
//
//        // 3) 응답
//        return ResponseEntity.ok(list);
//    }

    // 목록 조회
    @GetMapping("/record/my")
    public List<StatusChangeListDto> changeList(@RequestParam Long id){
        List<StatusChangeListDto> list = this.statusService.findMyList(id);

        return list;
    }

    // 상세: React ChangeStatusDetail (GET /api/student/record/{recordId})
    @GetMapping("/record/{recordId}")
    public ResponseEntity<StatusChangeRequestDto> getChangeRequestDetail(@PathVariable Long recordId) {
        StatusChangeRequestDto detail = statusService.getChangeRequestDetail(recordId);
        if (detail == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(detail);
    }

    // 삭제 핸들러 기능
    @DeleteMapping("/record/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long recordId) {
        boolean deleted = statusService.deleteChangeRequest(recordId);
        if (deleted) {
            return ResponseEntity.ok().body("삭제 성공");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 요청입니다");
        }
    }
}
