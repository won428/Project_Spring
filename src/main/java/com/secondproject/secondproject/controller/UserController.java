package com.secondproject.secondproject.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.swing.plaf.OptionPaneUI;
import java.net.URI;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MajorService majorService;
    private final CollegeService collegeService;
    private final PasswordEncoder passwordEncoder;
    private final LectureService lectureService;

    @PostMapping("/signup")
    public ResponseEntity<?> insertUser(@RequestBody UserDto userinfo){
        User user = new User();
        Major major = this.majorService.findMajor(userinfo.getMajor());
        String encodePassWord  = passwordEncoder.encode(userinfo.getPhone());

        user.setName(userinfo.getU_name());
        user.setGender(userinfo.getGender());
        user.setEmail(userinfo.getEmail());
        user.setBirthDate(userinfo.getBirthdate());
        user.setPassword(encodePassWord);
        user.setMajor(major);
        user.setPhone(userinfo.getPhone());
        user.setType(userinfo.getU_type());

        this.userService.insertUser(user);

        return ResponseEntity.ok(200);
    }

    // 관리자용 유저 목록 조회
    @GetMapping("/list")
    public List<UserListDto> userList(){
        List<UserListDto> userList = this.userService.findUserList();

        return userList;
    }

    // 학과별 교수 목록
    @GetMapping("/professorList")
    public List<UserListDto> professorList(@RequestParam("major_id") Long majorId){
        List<UserListDto> userList = this.userService.findProfessorList(majorId);

        return userList;
    }

    // 유저코드로 유저 찾기
    @GetMapping("/selectUserCode/{id}")
    public UserUpdateDto findByUsercode(@PathVariable Long id){

        UserUpdateDto userDto = new UserUpdateDto();
        Optional<User> optUser = this.userService.findByUsercode(id);
        User user = optUser
                .orElseThrow(()->
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

    // 관리자용 유저 정보수정
    @PatchMapping("/admin/update/{id}")
    public ResponseEntity<?> userUpdateByAdmin(@PathVariable Long id, @RequestBody UserUpdateDto userReactDto){

        User findUser = this.userService.findByUsercode(id)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, id + "사용자 없음"));
        Major major = this.majorService.findMajor(userReactDto.getMajor());

        this.userService.save(id, userReactDto, findUser , major);

        return ResponseEntity.ok(200);
    }

    // 학생 일괄 등록(페이지로 넘기기, DB 저장 X)
    //consumes = MediaType.MULTIPART_FORM_DATA_VALUE <- 파일 업로드 형식 요청만 받겠다는 뜻 / Json 요청은 거부
    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UserStBatchDto> studentListInsert(@RequestPart("file")MultipartFile file){
        return userService.parse(file);
    }

    // 학생 일괄 저장(DB에 저장)
    @PostMapping("/import")
    public ResponseEntity<?> inserBatchUser(@RequestBody @Valid List<UserStBatchDto> users, HttpServletRequest request){
        try {
            userService.importUsers(users);
            return ResponseEntity.ok().build();

            // 서비스에서 명시적으로 던진 상태예외
        } catch (ResponseStatusException ex) {
            ProblemDetail pd = ex.getBody();
            pd.setProperty("path", request.getRequestURI());
            pd.setProperty("timestamp", Instant.now().toString());
            return ResponseEntity.status(ex.getStatusCode()).body(pd);
        }
    }

}
