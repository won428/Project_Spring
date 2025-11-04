package com.secondproject.secondproject.Test;

import com.secondproject.secondproject.Enum.CompletionDiv;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class GradeInsert extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LectureRepository lectureRepository;
    @Autowired
    private GradeRepository gradeRepository;

    @Test
    public void seedGradesForUser1() {
        // 1) 성적의 대상 '학생' 사용자 조회 (필요 시 이메일/ID 조정)
        User user = userRepository.findByEmail("younghee.kim@example.com")
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 2) 기존 강의 조회 (예: ID 10)
        Lecture lecture = lectureRepository.findById(7L)
                .orElseThrow(() -> new RuntimeException("강의 없음"));

        // 3) Grade 생성 및 저장
        Grade g = new Grade();
        g.setUser(user);          // 성적 주체가 학생이라면 학생 User 설정
        g.setLecture(lecture);    // 반드시 null이 아니어야 함
        g.setAScore(new BigDecimal("10.00"));
        g.setAsScore(new BigDecimal("20.00"));
        g.setTScore(new BigDecimal("30.00"));
        g.setFtScore(new BigDecimal("40.00"));
        g.setTotalScore(new BigDecimal("0.00"));
        g.setLectureGrade(null);

        gradeRepository.save(g);
    }

    @Test
    public void seedGradesForUser2() {
        // 1) 성적의 대상 '학생' 사용자 조회 (필요 시 이메일/ID 조정)
        User user = userRepository.findByEmail("younghee.kim@example.com")
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 2) 기존 강의 조회 (예: ID 10)
        Lecture lecture = lectureRepository.findById(8L)
                .orElseThrow(() -> new RuntimeException("강의 없음"));

        // 3) Grade 생성 및 저장
        Grade g = new Grade();
        g.setUser(user);          // 성적 주체가 학생이라면 학생 User 설정
        g.setLecture(lecture);    // 반드시 null이 아니어야 함
        g.setAScore(new BigDecimal("10.00"));
        g.setAsScore(new BigDecimal("20.00"));
        g.setTScore(new BigDecimal("10.00"));
        g.setFtScore(new BigDecimal("20.00"));
        g.setTotalScore(new BigDecimal("0.00"));
        g.setLectureGrade(null);

        gradeRepository.save(g);
    }

    @Test
    public void seedGradesForUser3() {
        // 1) 성적의 대상 '학생' 사용자 조회 (필요 시 이메일/ID 조정)
        User user = userRepository.findByEmail("younghee.kim@example.com")
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 2) 기존 강의 조회 (예: ID 10)
        Lecture lecture = lectureRepository.findById(4L)
                .orElseThrow(() -> new RuntimeException("강의 없음"));

        // 3) Grade 생성 및 저장
        Grade g = new Grade();
        g.setUser(user);          // 성적 주체가 학생이라면 학생 User 설정
        g.setLecture(lecture);    // 반드시 null이 아니어야 함
        g.setAScore(new BigDecimal("50.00"));
        g.setAsScore(new BigDecimal("30.00"));
        g.setTScore(new BigDecimal("70.00"));
        g.setFtScore(new BigDecimal("90.00"));
        g.setTotalScore(new BigDecimal("0.00"));
        g.setLectureGrade(null);

        gradeRepository.save(g);
    }

    @Test
    public void seedGradesForUser4() {
        // 1) 성적의 대상 '학생' 사용자 조회 (필요 시 이메일/ID 조정)
        User user = userRepository.findByEmail("younghee.kim@example.com")
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 2) 기존 강의 조회 (예: ID 10)
        Lecture lecture = lectureRepository.findById(5L)
                .orElseThrow(() -> new RuntimeException("강의 없음"));

        // 3) Grade 생성 및 저장
        Grade g = new Grade();
        g.setUser(user);          // 성적 주체가 학생이라면 학생 User 설정
        g.setLecture(lecture);    // 반드시 null이 아니어야 함
        g.setAScore(new BigDecimal("40.00"));
        g.setAsScore(new BigDecimal("30.00"));
        g.setTScore(new BigDecimal("60.00"));
        g.setFtScore(new BigDecimal("50.00"));
        g.setTotalScore(new BigDecimal("0.00"));
        g.setLectureGrade(null);

        gradeRepository.save(g);
    }

    @Test
    public void seedGradesForUser5() {
        // 1) 성적의 대상 '학생' 사용자 조회 (필요 시 이메일/ID 조정)
        User user = userRepository.findByEmail("younghee.kim@example.com")
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 2) 기존 강의 조회 (예: ID 10)
        Lecture lecture = lectureRepository.findById(6L)
                .orElseThrow(() -> new RuntimeException("강의 없음"));

        // 3) Grade 생성 및 저장
        Grade g = new Grade();
        g.setUser(user);          // 성적 주체가 학생이라면 학생 User 설정
        g.setLecture(lecture);    // 반드시 null이 아니어야 함
        g.setAScore(new BigDecimal("40.00"));
        g.setAsScore(new BigDecimal("30.00"));
        g.setTScore(new BigDecimal("60.00"));
        g.setFtScore(new BigDecimal("50.00"));
        g.setTotalScore(new BigDecimal("0.00"));
        g.setLectureGrade(null);

        gradeRepository.save(g);
    }

    @Test
    public void seedGradesForUser7() {
        // 1) 성적의 대상 '학생' 사용자 조회 (필요 시 이메일/ID 조정)
        User user = userRepository.findByEmail("myungsoo.kim@example.com")
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 2) 기존 강의 조회 (예: ID 10)
        Lecture lecture = lectureRepository.findById(8L)
                .orElseThrow(() -> new RuntimeException("강의 없음"));

        // 3) Grade 생성 및 저장
        Grade g = new Grade();
        g.setUser(user);          // 성적 주체가 학생이라면 학생 User 설정
        g.setLecture(lecture);    // 반드시 null이 아니어야 함
        g.setAScore(new BigDecimal("50.00"));
        g.setAsScore(new BigDecimal("60.00"));
        g.setTScore(new BigDecimal("30.00"));
        g.setFtScore(new BigDecimal("70.00"));
        g.setTotalScore(new BigDecimal("0.00"));
        g.setLectureGrade(null);

        gradeRepository.save(g);
    }
}

