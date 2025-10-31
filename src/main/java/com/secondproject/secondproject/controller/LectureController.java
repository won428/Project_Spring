package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.UserRepository;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.dto.UserDto;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.MajorService;
import com.secondproject.secondproject.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {
    private final MajorService majorService;
    private final LectureService lectureService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;

    // 수강신청 관련해서 나중에 수강신청 컨트롤러로 이식할게요.

    // 관리자용 강의 등록
    @PostMapping("/admin/lectureRegister")
    public ResponseEntity<?> lectureRegisterByAdmin(@RequestBody LectureDto lectureDto) {

        this.lectureService.insertByAdmin(lectureDto);

        return ResponseEntity.ok(200);
    }

    // 강의 목록
    @GetMapping("/list")
    public List<LectureDto> lectureList() {
        List<LectureDto> lectureDtoList = this.lectureService.findAll();

        return lectureDtoList;
    }


    @PutMapping("/request")
    public ResponseEntity<?> lectureRequest(@RequestParam Long id, @RequestParam("status") Status status) {

        if (id == null || id < 0 || status == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "존재하지 않는 강의이거나 존재하지 않는 상태 표시입니다."));
        }

        this.lectureService.updateStatus(id, status);

        return ResponseEntity.ok(200);
    }

    // 일괄 신청(수강신청 컨트롤러로 추후에 이식)
    @PostMapping("/apply")
    public ResponseEntity<?> applyLecture(@RequestBody List<Long> idList, @RequestParam Long id) {


        try {
            this.lectureService.applyLecture(idList, id);
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



    // 단일 신청(추후에 수강신청 컨트롤러로 이식)
    @PostMapping("/applyOne")
    public ResponseEntity<?> applyLectureOne(@RequestBody Long lecId, @RequestParam Long id) {
        try {
            this.lectureService.applyLectureOne(lecId, id);
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

    // 학생 수강신청한 목록
    @GetMapping("/mylist")
    public List<LectureDto> myLectureList(@RequestParam Long userId) {

        List<LectureDto> myLecture = this.lectureService.myLectureList(userId);

        return myLecture;
    }

    // 학생 수강신청 가능 목록
    @GetMapping("/apply/list")
    public List<LectureDto> applyLectureList(@RequestParam Long id){
        List <LectureDto> lectureList = this.lectureService.applyLecturList(id);

        return lectureList;
    }

    // 교수 개설된 강의 상세정보
    @GetMapping("/detail/{id}")
    public LectureDto lectureDetailForPro(@PathVariable Long id) {
        LectureDto lectureDto = lectureService.findByID(id);

        return lectureDto;
    }

    // 강의 상세정보 학생 목록
    @GetMapping("/detail/studentList/{id}")
    public List<UserDto> detailStudentList(@PathVariable Long id) {
        List<UserDto> userDtos = this.userService.findUserLectureDetail(id);

        return userDtos;
    }

    //강의실 목록
    @GetMapping("/List")
    @Transactional
    public ResponseEntity<List<LectureDto>> GiveLectureList(@RequestParam String email) {

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> (new UsernameNotFoundException("존재하지 않는 사용자 입니다.")));

        List<LectureDto> lectureListDto = lectureService.findByUser(user);


        return ResponseEntity.ok(lectureListDto);
    }

    @GetMapping("/spec")
    public ResponseEntity<?> LectureSpec(@RequestParam Long id) {
        try {
            LectureDto lecture = lectureService.LectureSpec(id);


            return ResponseEntity.ok(lecture);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("강의를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류 발생");
        }
    }

    // 관리자가 승인이 완료되고 정원이 모두 채워진 강의를 개강으로 전환합니다.
    @PatchMapping("/inprogress")
    public ResponseEntity<?> lectureInprogress(@RequestBody List<Long> idList, @RequestParam Status status){
        try {
            this.lectureService.lectureInprogress(idList, status);
            return ResponseEntity.ok(200);
        }catch (ResponseStatusException ex) {
            try {

                int errStatus = ex.getStatusCode().value();
                String error = HttpStatus.valueOf(errStatus).name();

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

    @GetMapping("/stlist")
    public ResponseEntity<?> StudentLecList(@RequestParam String email) {
        try {
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> (new UsernameNotFoundException("존재하지 않는 사용자 입니다.")));

            List<LectureDto> lectureListDto = lectureService.findByStudent(user);
            return ResponseEntity.ok(lectureListDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류 발생");
        }


    }
}
