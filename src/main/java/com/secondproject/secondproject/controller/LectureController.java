package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Enum.CompletionDiv;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.LecScheduleRepository;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.UserRepository;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.dto.UserDto;
import com.secondproject.secondproject.service.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {
    private final MajorService majorService;
    private final LectureService lectureService;
    private final UserService userService;
    private final AttendanceStudentService attendanceStudentService;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LecScheduleRepository lecScheduleRepository;

    // 수강신청 관련해서 나중에 수강신청 컨트롤러로 이식할게요.


    @GetMapping("/pageList")
    public ResponseEntity<Page<LectureDto>> lectureListPage(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) CompletionDiv searchCompletionDiv,
            @RequestParam(required = false) Long searchMajor,
            @RequestParam(required = false) Integer searchCredit,
            @RequestParam(required = false) String searchStartDate,
            @RequestParam(required = false) String searchMode,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) DayOfWeek searchSchedule,
            @RequestParam(required = false) String searchYear,
            @RequestParam(required = false) Integer searchLevel,
            @RequestParam(required = false) Long searchUser,
            @RequestParam(required = false) Status searchStatus
    ) {
        LecturePageListDto lecturePageListDto = new LecturePageListDto(pageNumber,pageSize,searchCompletionDiv, searchMajor, searchCredit, searchStartDate, searchMode,searchKeyword,searchSchedule,searchYear,searchLevel,searchUser,searchStatus);
        Page<LectureDto> lectureList = this.lectureService.listPageLecture(lecturePageListDto, pageNumber, pageSize);

        System.out.println("검색 조건 : " + lecturePageListDto);
        System.out.println("총 상품 개수 : " + lectureList.getTotalElements());
        System.out.println("총 페이지 번호 : " + lectureList.getTotalPages());
        System.out.println("현재 페이지 번호 : " + lectureList.getNumber());


        return ResponseEntity.ok(lectureList);
    }

    @PatchMapping("/lectureUpdate")
    public ResponseEntity<?> updateLecture(
            @RequestPart LectureDto lecture,
            @RequestPart List<LectureScheduleDto> schedule,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart PercentDto percent,
            @RequestPart List<AttachmentDto> existingDtos
    ) {
        try {
            this.lectureService.updateLecture(lecture, schedule, files, percent, existingDtos);
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

    //업데이트용 단일 강의 정보
    @GetMapping("/findOne/{id}")
    public LectureDto findLectureForUpdate(@PathVariable Long id) {
        LectureDto lectureDto = this.lectureService.findByID(id);

        return lectureDto;
    }

    //모달 단일 강의정보
    @GetMapping("/info")
    public LectureDto getLectureInfo(@RequestParam Long modalId) {
        LectureDto lectureDto = this.lectureService.findByID(modalId);

        return lectureDto;
    }

    // 강의 등록
    @PostMapping("/lectureRegister")
    public ResponseEntity<?> lectureRegisterByAdmin(
            @RequestPart LectureDto lecture,
            @RequestPart List<LectureScheduleDto> schedule,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart PercentDto percent
    ) {

        try {
            this.lectureService.insertByAdmin(lecture, schedule, files, percent);
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

    @PutMapping("/restart")
    public ResponseEntity<?> restart(@RequestParam Long id, @RequestParam("status") Status status) {

        if (id == null || id < 0 || status == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "존재하지 않는 강의이거나 존재하지 않는 상태 표시입니다."));
        }

        this.lectureService.lectureRestart(id, status);

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
    public List<LectureDto> applyLectureList(@RequestParam Long id) {
        List<LectureDto> lectureList = this.lectureService.applyLecturList(id);

        return lectureList;
    }

    // 수강신청 후 개강, 종강, 거부된 목록
    @GetMapping("/mylist/completed")
    public List<LectureDto> applyLectureListEnd(@RequestParam Long userId) {
        List<LectureDto> lectureList = this.lectureService.applyLecturListEnd(userId);

        return lectureList;
    }

    // 교수 개설된 강의 상세정보
    @GetMapping("/detail/{id}")
    public LectureDto lectureDetailForPro(@PathVariable Long id) {
        LectureDto lectureDto = lectureService.findByID(id);

        return lectureDto;
    }

    // 순수 강의 정보 - 첨부파일 미포함
    @GetMapping("/detailLecture/{id}")
    public LectureDto lectureDetail(@PathVariable Long id) {
        LectureDto lectureDto = lectureService.findBylectureID(id);

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
    public ResponseEntity<List<LectureDto>> GiveLectureList(@RequestParam String username, @RequestParam String sortKey) {
        try {
            System.out.println(sortKey);
            Long userCode = Long.parseLong(username);
            User user = userService.findByUsercode(userCode)
                    .orElseThrow(() -> (new UsernameNotFoundException("존재하지 않는 사용자 입니다.")));

            List<LectureDto> lectureListDto = lectureService.findByUser(user, sortKey);

            return ResponseEntity.ok(lectureListDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @GetMapping("/stlist")
    public ResponseEntity<?> StudentLecList(@RequestParam String username, @RequestParam String sortKey) {
        try {
            Long userCode = Long.parseLong(username);
            User user = userService.findByUsername(userCode)
                    .orElseThrow(() -> (new UsernameNotFoundException("존재하지 않는 사용자 입니다.")));

            List<LectureDto> lectureListDto = lectureService.findByStudent(user, sortKey);
            return ResponseEntity.ok(lectureListDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류 발생");
        }

    }

    // 강의 회차 목록 - 강의테이블에서 강의일, 요일, 교시 리스트로 받아오기
    @GetMapping("/{id}/sessions")
    public ResponseEntity<LecSessionResponseDto> selectLectureSession(
            @PathVariable Long id,
            @ModelAttribute LecSessionRequestDto requestDto) {
        // 해당 강의 id
        lectureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        LecSessionResponseDto responseDtos = lectureService.selectSessions(id, requestDto);

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(lectureService.getSchedule(id));
    }

    // 강의 상세정보 '수강 중인' 학생 목록 (Enrollment 기준)
    @GetMapping("/detail/enrolledStudentList/{id}")
    public List<UserDto> detailEnrolledStudentList(@PathVariable Long id) {
        return this.userService.findEnrolledUserLectureDetail(id);
    }

    // 해당 강의 수강중인 학생리스트 출결 등록
    @PostMapping("/{id}/insertAttendances")
    public ResponseEntity<List<AttendanceResponseDto>> insertAttendances(
            @PathVariable Long id,
            @RequestBody List<AttendanceRequestDto> requestDtos) {

        log.info(">>> HIT insertAttendances id={}, bodySize={}", id, (requestDtos == null ? 0 : requestDtos.size()));

        List<AttendanceResponseDto> responseDtos = attendanceStudentService.insertAttendances(id, requestDtos);

        return ResponseEntity.ok(responseDtos);
    }

    // 학생 출결 저장 후 라디오 영구 비활성화를 위한 메소드
    @GetMapping("/{id}/attendance/finalized")
    public ResponseEntity<Map<String, Object>> isFinalized(@PathVariable Long id, @RequestParam("date") @org.springframework.format.annotation.DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate sessionDate) {
        boolean finalized = attendanceStudentService.isFinalizedAny(id, sessionDate);
        return ResponseEntity.ok().body(Map.of("finalized", finalized));
    }

    // 출결 목록 불러오기
    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<AttendanceResponseDto>> getAttendances(
            @PathVariable Long id,
            @RequestParam("date")
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            LocalDate sessionDate) {

        return ResponseEntity.ok(attendanceStudentService.getAttendances(id, sessionDate));
    }

    // 학생 1명이 수강중인 모든 강의 불러오기


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

    // 관리자가 승인이 완료되고 정원이 모두 채워진 강의 묶음을 개강으로 전환합니다.
    @PatchMapping("/inprogress")
    public ResponseEntity<?> lectureInprogress(@RequestBody List<Long> idList, @RequestParam Status status) {
        try {
            this.lectureService.lectureInprogress(idList, status);
            return ResponseEntity.ok(200);
        } catch (ResponseStatusException ex) {
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

    @PatchMapping("/reinprogress")
    public ResponseEntity<?> reInprogress(@RequestBody List<Long> idList, @RequestParam Status status) {
        try {
            this.lectureService.reInprogress(idList, status);
            return ResponseEntity.ok(200);
        } catch (ResponseStatusException ex) {
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


    @PatchMapping("/status/admin")
    public ResponseEntity<?> lecturesChangeStatus(@RequestBody List<Long> idList, @RequestParam Status status) {
        try {
            this.lectureService.lectureChangeStatus(idList, status);
            return ResponseEntity.ok(200);
        } catch (ResponseStatusException ex) {
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

    @GetMapping("/regForPro/{id}")
    public ProRegDto proRegDto(@PathVariable Long id) {
        ProRegDto proRegDto = this.userService.findProfessor(id);

        return proRegDto;
    }


}
