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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // 유저 등록
    @PostMapping("/signup")
    public ResponseEntity<?> insertUser(@RequestBody UserDto userinfo){



        try {
            this.userService.insertUser(userinfo);
            return ResponseEntity.ok(Map.of("success",true));
        }catch (ResponseStatusException ex){
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
            }catch (Exception otherEx){
                return ResponseEntity.status(500).body("네트워크 오류");
            }
        }
    }

    // 관리자용 유저 목록 조회
    @GetMapping("/list")
    public List<UserListDto> userList(){
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
            @RequestParam(required = false) String searchKeyword
    ){
        UserListSearchDto userListSearchDto = new UserListSearchDto(searchMajor, searchGender, searchUserType, searchMode, searchKeyword);
        Page<UserListDto> userList = this.userService.ListPageUser(userListSearchDto, pageNumber, pageSize);

        System.out.println("검색 조건 : " + userListSearchDto);
        System.out.println("총 상품 개수 : " + userList.getTotalElements());
        System.out.println("총 페이지 번호 : " + userList.getTotalPages());
        System.out.println("현재 페이지 번호 : " + userList.getNumber());


        return ResponseEntity.ok(userList);
    }

    // 학과별 교수 목록
    @GetMapping("/professorList")
    public List<UserListDto> professorList(@RequestParam("major_id") Long majorId){
        List<UserListDto> userList = this.userService.findProfessorList(majorId);

        return userList;
    }

    // 유저코드로 유저 찾기(컨트롤러 부분 나중에 서비스로 이식할겁니다.)
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

    // 관리자용 유저 정보수정(컨트롤러 부분 나중에 서비스로 이식할겁니다.)
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

    @GetMapping("/api/student/info")
    public ResponseEntity<?> getStudentInfo(Authentication authentication) {
        // 1) 인증 여부 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "인증 정보가 없습니다."));
        }

        // 2) 이메일(=username)로 사용자 조회
        String email = authentication.getName();
        User user = studentService.getStudentByEmail(email);

        // 3) STUDENT 타입만 접근 허용
        if (user == null || user.getType() != STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "학생 정보만 조회할 수 있습니다."));
        }

        // 4) 학적 상태 조회
        StatusRecords statusRecord = studentService.getStatusRecordByUserId(user.getId());

        // 5) DTO 생성(생성자에서 모든 필드 매핑)
        StudentInfoDto dto = new StudentInfoDto(user, statusRecord);

        // 6) 프론트 기대 형태로 감싸서 반환
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("statusid", dto.getStatusId());
        statusMap.put("studentStatus", dto.getStudent_status());
        statusMap.put("admissionDate", dto.getAdmissionDate());
        statusMap.put("leaveDate", dto.getLeaveDate());
        statusMap.put("returnDate", dto.getReturnDate());
        statusMap.put("graduationDate", dto.getGraduationDate());
        statusMap.put("retentionDate", dto.getRetentionDate());
        statusMap.put("expelledDate", dto.getExpelledDate());
        statusMap.put("majorCredit", dto.getMajorCredit());
        statusMap.put("generalCredit", dto.getGeneralCredit());
        statusMap.put("totalCredit", dto.getTotalCredit());
        statusMap.put("currentCredit", dto.getCurrentCredit());
        statusMap.put("studentImage", dto.getStudentImage());

        Map<String, Object> studentMap = new HashMap<>();
        studentMap.put("userid", dto.getId());
        studentMap.put("userCode", dto.getUserCode());
        studentMap.put("name", dto.getName());
        studentMap.put("password", dto.getPassword());
        studentMap.put("birthDate", dto.getBirthDate());
        studentMap.put("email", dto.getEmail());
        studentMap.put("phone", dto.getPhone());
        studentMap.put("gender", dto.getGender());
        // major는 엔티티 전체 직렬화 시 순환 참조 위험이 있을 수 있으므로 필요한 필드만 노출 권장
        // 예: 학과명만 사용한다면 아래처럼 가공
        studentMap.put("major", dto.getMajor()); // 필요 시 Map.of("name", dto.getMajor().getName()) 로 축소
        // type은 Enum → 문자열로 직렬화되므로 프론트 비교가 가능
        studentMap.put("type", dto.getType());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("type", dto.getType()); // "STUDENT"
        responseBody.put("studentInfo", studentMap);
        responseBody.put("statusRecords", statusMap);

        return ResponseEntity.ok(responseBody);
    }


}
