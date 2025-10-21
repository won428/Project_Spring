package com.secondproject.secondproject.Service;

import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.Repository.StatusRecordsRepository;
import com.secondproject.secondproject.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StatusRecordsRepository statusRecordsRepository;

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

        String stringStudentId =  stringYear + stringId + stringMajor;

        Long studentId = Long.parseLong(stringStudentId);

        saved.setUser_code(studentId);
    }
}
