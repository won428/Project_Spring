package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.UserDto;
import com.secondproject.secondproject.dto.UserListDto;
import com.secondproject.secondproject.dto.UserUpdateDto;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StatusRecordsRepository statusRecordsRepository;
    private final MajorRepository majorRepository;
    private final CollegeRepository collegeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRegRepository courseRegRepository;

    @Transactional
    public void insertUser(User newUser) {
        User saved = this.userRepository.save(newUser);
        StatusRecords userStatus = new StatusRecords();

        userStatus.setUser(saved);
        userStatus.setAdmissionDate(LocalDate.now());

        StatusRecords savedRecords = statusRecordsRepository.save(userStatus);

        LocalDate b = savedRecords.getAdmissionDate();     // 1997-04-28
        Long id = saved.getId();
        int year = b.getYear();                // 입학년도
        Long major = saved.getMajor().getId(); // 학과 코드

        String stringYear = Integer.toString(year); // 입학년도 문자열로 변환
        String stringMajor = Long.toString(major); // 학과 코드 문자열로 변환
        String stringId = Long.toString(id); // id 문자열로 변환

        String stringStudentId = stringYear + stringId + stringMajor;

        Long studentId = Long.parseLong(stringStudentId);

        saved.setUserCode(studentId);
    }

    // 전체 유저 조회
    public List<UserListDto> findUserList() {
        List<User> userList = this.userRepository.findAll();
        List<UserListDto> userListDto = new ArrayList<>();


        for (User user : userList) {
            UserListDto userDto = new UserListDto();
            Major major = this.majorRepository.findMajorById(user.getMajor().getId());
            College college = this.collegeRepository.findCollegeById(major.getCollege().getId());

            String majorName = major.getName();
            String collegeName = college.getType();

            userDto.setU_name(user.getName());
            userDto.setBirthdate(user.getBirthDate());
            userDto.setGender(user.getGender());
            userDto.setUser_code(user.getUserCode());
            userDto.setPhone(user.getPhone());
            userDto.setEmail(user.getEmail());
            userDto.setMajor(majorName);
            userDto.setCollege(collegeName);
            userDto.setU_type(user.getType());

            userListDto.add(userDto);
        }

        return userListDto;
    }

    public List<UserListDto> findProfessorList(Long majorId) {
        Major findmajor = this.majorRepository.findMajorById(majorId);
        List<User> userList = this.userRepository.findAllByMajor(findmajor);
        List<UserListDto> userListDto = new ArrayList<>();


        for (User user : userList) {
            UserListDto userDto = new UserListDto();
            Major major = this.majorRepository.findMajorById(user.getMajor().getId());
            College college = this.collegeRepository.findCollegeById(major.getCollege().getId());
            if (user.getType() != UserType.PROFESSOR) continue;
            String majorName = major.getName();
            String collegeName = college.getType();

            userDto.setId(user.getId());
            userDto.setU_name(user.getName());
            userDto.setBirthdate(user.getBirthDate());
            userDto.setGender(user.getGender());
            userDto.setUser_code(user.getUserCode());
            userDto.setPhone(user.getPhone());
            userDto.setEmail(user.getEmail());
            userDto.setPassword(user.getPassword());
            userDto.setMajor(majorName);
            userDto.setCollege(collegeName);
            userDto.setU_type(user.getType());

            userListDto.add(userDto);

        }

        return userListDto;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public Optional<User> getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    public void setPassword(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByUsercode(Long userCode) {
        Optional<User> user = this.userRepository.findByUserCode(userCode);

        return user;
    }


    public void update(Long id, UserUpdateDto userReactDto, User findUser, Major major) {

        User user = findUser;
        String password = passwordEncoder.encode(userReactDto.getPassword());

        if (userReactDto.getPassword() != null && !userReactDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userReactDto.getPassword()));
        }

        if (userReactDto.getGender() != null) user.setGender(userReactDto.getGender());
        if (userReactDto.getPassword() != null) user.setPassword(password);
        if (userReactDto.getMajor() != null) user.setMajor(major);
        if (userReactDto.getEmail() != null) user.setEmail(userReactDto.getEmail());
        if (userReactDto.getPhone() != null) user.setPhone(userReactDto.getPhone());
        if (userReactDto.getBirthdate() != null) user.setBirthDate(userReactDto.getBirthdate());
        if (userReactDto.getU_type() != null) user.setType(userReactDto.getU_type());

        this.userRepository.save(user);
    }

    public List<UserDto> findUserLectureDetail(Long lectureId) {
        List<CourseRegistration> courseRegistrations = this.courseRegRepository.findByLecture_Id(lectureId);
        List<UserDto> userDtoList = new ArrayList<>();

        for (CourseRegistration courseRegistration: courseRegistrations){
            User user = this.userRepository.findById(courseRegistration.getUser().getId())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"없는 유저"));
            UserDto userDto = new UserDto();

            userDto.setUserCode(user.getUserCode());
            userDto.setU_name(user.getName());
            userDto.setMajorName(user.getMajor().getName());
            userDto.setEmail(user.getEmail());
            userDto.setPhone(user.getPhone());

            userDtoList.add(userDto);
        }

        return userDtoList;
    }
}
