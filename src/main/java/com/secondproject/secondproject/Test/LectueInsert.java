package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.OnlineLectureRepository;
import com.secondproject.secondproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class LectueInsert extends AbstractTestNGSpringContextTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OnlineLectureRepository onlineLectureRepository;
    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertLectureList() {
        Lecture lecture = new Lecture();
        User user = userRepository.findByEmail("PRO123@PRO123")
                .orElseThrow(() -> new RuntimeException());
        lecture.setName("일반물리학1");
        lecture.setUser(user);
        lecture.setCredit(3);
        lecture.setStartDate(LocalDate.now());
        lecture.setDescription("123123");

        lecture.setMajor(user.getMajor());
        lecture.setTotalStudent(40);
        lecture.setStatus(Status.COMPLETED);

        lectureRepository.save(lecture);

    }

    @Test
    public void OnlineLecture() {
        User user = userRepository.findByEmail("PRO123@PRO123")
                .orElseThrow(() -> new RuntimeException());

        List<Lecture> lecture = lectureRepository.findByUser(user);
        for (Lecture lec : lecture) {
            OnlineLecture onlineLecture = new OnlineLecture();
            onlineLecture.setLecture(lec);
            onlineLecture.setName(lec.getName());
            onlineLecture.setDisable(true);
            onlineLecture.setUpdatedDate(LocalDate.now());
            onlineLecture.setPath("....");
            onlineLectureRepository.save(onlineLecture);
        }

    }

}
