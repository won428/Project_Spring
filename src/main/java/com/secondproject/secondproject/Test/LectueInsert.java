package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.math.BigDecimal;
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
    private GradeRepository gradeRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
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

    @Test
    public void GradeInsert() {
        User user = userRepository.findByEmail("Student123@Student123")
                .orElseThrow(() -> new RuntimeException());
        Lecture lecture = lectureRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException());
        Grade grade = new Grade();

        grade.setUser(user);
        grade.setLecture(lecture);
        grade.setAScore(BigDecimal.ONE);
        grade.setAsScore(BigDecimal.ONE);
        grade.setTScore(BigDecimal.ONE);
        grade.setFtScore(BigDecimal.ONE);
        grade.setTotalScore(BigDecimal.ONE);
        grade.setLectureGrade("A");
        gradeRepository.save(grade);

//        private User user;
//        private Lecture lecture;
//        private BigDecimal aScore = BigDecimal.ZERO; // 출석 점수
//        private BigDecimal asScore = BigDecimal.ZERO; // 과제 점수
//        private BigDecimal tScore = BigDecimal.ZERO; // 중간 점수
//        private BigDecimal ftScore = BigDecimal.ZERO; // 기말 점수
//        private BigDecimal totalScore = BigDecimal.ZERO; // 총점
//        private String lectureGrade; // 학점
    }


    @Test
    public void Enrollment() {

        User user = userRepository.findByEmail("Student123@Student123")
                .orElseThrow(() -> new RuntimeException());
        Lecture lecture = lectureRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException());

        Enrollment enrollment = new Enrollment();

        Grade grade = gradeRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException());
        ;

        enrollment.setUser(user);
        enrollment.setGrade(grade);
        enrollment.setLecture(lecture);
        enrollment.setStatus(Status.PENDING);
        enrollmentRepository.save(enrollment);

    }


}
