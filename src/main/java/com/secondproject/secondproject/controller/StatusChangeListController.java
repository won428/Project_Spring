//// src/main/java/com/secondproject/secondproject/controller/StudentRecordController.java
//package com.secondproject.secondproject.controller;
//
//import com.secondproject.secondproject.dto.StatusChangeListDto;
//import com.secondproject.secondproject.entity.User;
//import com.secondproject.secondproject.repository.UserRepository;
//import com.secondproject.secondproject.service.StudentRecordService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
///**
// * 학적변경신청 "목록" 전용 컨트롤러 (리스트 출력만 담당)
// * - GET /api/student/record/my
// * - 응답: Page<StatusChangeListDto>  (React는 data.content 사용)
// * - 별도 유틸/새 클래스 없이 Authentication에서 name(=username/email)로 현재 사용자 식별
// */
//@RestController
//@RequestMapping("/api/student/record")
//@RequiredArgsConstructor
//public class StudentRecordController {
//
//    private final StudentRecordService studentRecordService;
//    private final UserRepository userRepository; // username/email -> userId 매핑
//
//    @GetMapping("/my")
//    public ResponseEntity<Page<StatusChangeListDto>> getMyRecords(Pageable pageable, Authentication auth) {
//        if (auth == null || auth.getName() == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 인증 없음 [web:333]
//        }
//
//        // JWT 필터가 설정한 Authentication의 name을 로그인 식별자로 사용 (이메일/아이디 등)
//        String usernameOrEmail = auth.getName(); // 프로젝트 규약에 맞게 이메일/아이디 중 선택 [web:333]
//
//        // 현재 사용자 조회(프로젝트에 맞는 조회 메서드로 교체: findByEmail 또는 findByUsername)
//        User me = userRepository.findByEmail(usernameOrEmail)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다.")); // [web:333]
//
//        Page<StatusChangeListDto> page = studentRecordService.getMyStatusChangeList(me.getId(), pageable); // 목록 전용 DTO 페이지 [web:244]
//        return ResponseEntity.ok(page); // 프론트는 data.content를 우선 소비 [web:247]
//    }
//}
