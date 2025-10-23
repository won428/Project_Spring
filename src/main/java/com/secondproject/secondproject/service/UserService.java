package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.UserListDto;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.CollegeRepository;
import com.secondproject.secondproject.repository.MajorRepository;
import com.secondproject.secondproject.repository.StatusRecordsRepository;
import com.secondproject.secondproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

//    public User setPasswordByEmail(String email) {
//        return userRepository.setPasswordByEmail(email);
//    }

    public Optional<User> getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

}