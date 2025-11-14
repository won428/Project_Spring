package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.service.CollegeService;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.MajorService;
import com.secondproject.secondproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.dto.StudentInfoDto;
import com.secondproject.secondproject.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.plaf.OptionPaneUI;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.net.URI;
import java.time.Instant;
import java.util.*;

import static com.secondproject.secondproject.Enum.UserType.STUDENT;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MajorService majorService;
    private final CollegeService collegeService;
    private final PasswordEncoder passwordEncoder;
    private final StudentService studentService;
    private final LectureService lectureService;

    //유저 학적 정보 가져오기
    @GetMapping("/detailAll/{id}")
    public UserDetailAllDto userDetailAll(@PathVariable Long id){
        UserDetailAllDto userDetailAllDto = this.userService.userDetailAll(id);

        return userDetailAllDto;
    }

    // 유저 등록
    @PostMapping("/signup")
    public ResponseEntity<?> insertUser(
            @ModelAttribute UserDto userinfo,
            @RequestParam(value = "files", required = false)  MultipartFile file
    ) {


        try {
            this.userService.insertUser(userinfo, file);
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
                return ResponseEntity.status(500).body("네트워크 오류");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 관리자용 유저 목록 조회
    @GetMapping("/list")
    public List<UserListDto> userList() {
        List<UserListDto> userList = this.userService.findUserList();

        return userList;
    }

    // 관리자용 전체 유저 목록 조회 페이징
    @GetMapping("/pageList")
    public ResponseEntity<Page<UserListDto>> userListPage(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(required = false) Long searchMajor,
            @RequestParam(required = false) String searchGender,
            @RequestParam(required = false) UserType searchUserType,
            @RequestParam(required = false) String searchMode,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) Long searchCollege,
            @RequestParam(required = false) Integer searchLevel
    ) {
        UserListSearchDto userListSearchDto = new UserListSearchDto(searchLevel,searchCollege,searchMajor, searchGender, searchUserType, searchMode, searchKeyword);
        Page<UserListDto> userList = this.userService.ListPageUser(userListSearchDto, pageNumber, pageSize);

        System.out.println("검색 조건 : " + userListSearchDto);
        System.out.println("총 상품 개수 : " + userList.getTotalElements());
        System.out.println("총 페이지 번호 : " + userList.getTotalPages());
        System.out.println("현재 페이지 번호 : " + userList.getNumber());


        return ResponseEntity.ok(userList);
    }

    // 학과별 교수 목록
    @GetMapping("/professorList")
    public List<UserListDto> professorList(@RequestParam("major_id") Long majorId) {
        List<UserListDto> userList = this.userService.findProfessorList(majorId);

        return userList;
    }

    // 유저코드로 유저 찾기
    @GetMapping("/selectUserCode/{id}")
    public UserUpdateDto findByUsercode(@PathVariable Long id) {

        UserUpdateDto userDto = new UserUpdateDto();
        Optional<User> optUser = this.userService.findByUsercode(id);
        User user = optUser
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, id + "사용자 없음"));
        College college = this.collegeService.getCollegeId(user.getMajor().getCollege().getId());

        userDto.setId(user.getId());
        userDto.setU_name(user.getName());
        userDto.setGender(user.getGender());
        userDto.setMajor(user.getMajor().getId());
        userDto.setCollege(college.getId());
        userDto.setPhone(user.getPhone());
        userDto.setEmail(user.getEmail());
        userDto.setBirthdate(user.getBirthDate());
        userDto.setPassword(user.getPassword());
        userDto.setUser_code(user.getUserCode());
        userDto.setU_type(user.getType());


        return userDto;
    }

    // 관리자용 유저 정보수정(컨트롤러 부분 나중에 서비스로 이식할겁니다.)
    @PatchMapping("/admin/update/{id}")
    public ResponseEntity<?> userUpdateByAdmin(@PathVariable Long id, @RequestBody UserUpdateDto userReactDto) {

        User findUser = this.userService.findByUsercode(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, id + "사용자 없음"));
        Major major = this.majorService.findMajor(userReactDto.getMajor());

        this.userService.update(id, userReactDto, findUser, major);

        return ResponseEntity.ok(200);
    }

    // 학생 일괄 등록(페이지로 넘기기, DB 저장 X)
    //consumes = MediaType.MULTIPART_FORM_DATA_VALUE <- 파일 업로드 형식 요청만 받겠다는 뜻 / Json 요청은 거부
    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UserStBatchDto> studentListInsert(@RequestPart("file") MultipartFile file) {
        return userService.parse(file);
    }


//        // 4) 학적 상태 조회 (최종적으로 이 형태로 바뀌어야 함)
//        StatusRecords statusRecord = studentService.getStatusRecordByUserId(user.getId());
//        // 학생 일괄 저장(DB에 저장)
//        @PostMapping("/import")
//        public ResponseEntity<?> inserBatchUser (@RequestBody @Valid List < UserStBatchDto > users, HttpServletRequest
//        request){
//            try {
//                userService.importUsers(users);
//                return ResponseEntity.ok().build();
//
//                // 서비스에서 명시적으로 던진 상태예외
//            } catch (ResponseStatusException ex) {
//                ProblemDetail pd = ex.getBody();
//                pd.setProperty("path", request.getRequestURI());
//                pd.setProperty("timestamp", Instant.now().toString());
//                return ResponseEntity.status(ex.getStatusCode()).body(pd);
//            }
//        }

}

