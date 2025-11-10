package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.Enum.CompletionDiv;
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
import java.time.LocalDateTime;
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
        User user = userRepository.findByEmail("PRO123@PR.com")
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
            onlineLecture.setTitle(lec.getName());
            onlineLecture.setDisable(true);
            onlineLecture.setUpdatedDate(LocalDateTime.now());
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
        grade.setLectureGrade(BigDecimal.ONE);
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

    @Test
    public void insertLectureDaisuHak1() {
        Lecture lecture = new Lecture();
        User user = userRepository.findByEmail("younghee.kim@example.com")
                .orElseThrow(() -> new RuntimeException());

        lecture.setName("대수학1");                     // 강의명
        lecture.setUser(user);                          // 교수 또는 담당자 정보 (User 엔티티)
        lecture.setCredit(3);                           // 학점
        lecture.setStartDate(LocalDate.now());          // 시작일 (예: 오늘 날짜)
        lecture.setDescription("대수학1 강의입니다.");  // 강의 설명

        lecture.setMajor(user.getMajor());              // 담당 전공
        lecture.setTotalStudent(40);                     // 총 수강생 수
        lecture.setStatus(Status.COMPLETED);             // 강의 상태 (예: COMPLETED, ONGOING 등)

        // 반드시 null 불가 컬럼인 completionDiv 설정 추가
        lecture.setCompletionDiv(CompletionDiv.MAJOR_REQUIRED);
        lectureRepository.save(lecture);                 // 강의 저장
    }

    @Test
    public void insertLectureComputer1() {
        Lecture lecture = new Lecture();
        User user = userRepository.findByEmail("younghee.kim@example.com")
                .orElseThrow(() -> new RuntimeException());

        lecture.setName("컴퓨터공학");                     // 강의명
        lecture.setUser(user);                          // 교수 또는 담당자 정보 (User 엔티티)
        lecture.setCredit(3);                           // 학점
        lecture.setStartDate(LocalDate.of(2025, 3, 2));
        lecture.setDescription("컴퓨터공학 강의입니다.");  // 강의 설명

        lecture.setMajor(user.getMajor());              // 담당 전공
        lecture.setTotalStudent(40);                     // 총 수강생 수
        lecture.setStatus(Status.COMPLETED);             // 강의 상태 (예: COMPLETED, ONGOING 등)

        // 반드시 null 불가 컬럼인 completionDiv 설정 추가
        lecture.setCompletionDiv(CompletionDiv.MAJOR_REQUIRED);
        lectureRepository.save(lecture);                 // 강의 저장
    }

    @Test
    public void insertLecture2() {
        Lecture lecture = new Lecture();
        User user = userRepository.findByEmail("younghee.kim@example.com")
                .orElseThrow(() -> new RuntimeException());

        lecture.setName("물리");                     // 강의명
        lecture.setUser(user);                          // 교수 또는 담당자 정보 (User 엔티티)
        lecture.setCredit(3);                           // 학점
        lecture.setStartDate(LocalDate.of(2024, 9, 5));
        lecture.setDescription("물리학 강의입니다.");  // 강의 설명

        lecture.setMajor(user.getMajor());              // 담당 전공
        lecture.setTotalStudent(40);                     // 총 수강생 수
        lecture.setStatus(Status.COMPLETED);             // 강의 상태 (예: COMPLETED, ONGOING 등)

        // 반드시 null 불가 컬럼인 completionDiv 설정 추가
        lecture.setCompletionDiv(CompletionDiv.MAJOR_REQUIRED);
        lectureRepository.save(lecture);                 // 강의 저장
    }

    @Test
    public void insertLecture3() {
        Lecture lecture = new Lecture();
        User user = userRepository.findByEmail("John.prof@univ.com")
                .orElseThrow(() -> new RuntimeException());

        lecture.setName("화학");                     // 강의명
        lecture.setUser(user);                          // 교수 또는 담당자 정보 (User 엔티티)
        lecture.setCredit(3);                           // 학점
        lecture.setStartDate(LocalDate.of(2025, 10, 5));
        lecture.setDescription("화학 강의입니다.");  // 강의 설명

        lecture.setMajor(user.getMajor());              // 담당 전공
        lecture.setTotalStudent(40);                     // 총 수강생 수
        lecture.setStatus(Status.COMPLETED);             // 강의 상태 (예: COMPLETED, ONGOING 등)

        // 반드시 null 불가 컬럼인 completionDiv 설정 추가
        lecture.setCompletionDiv(CompletionDiv.MAJOR_REQUIRED);
        lectureRepository.save(lecture);                 // 강의 저장
    }

    @Test
    public void insertLecture4() {
        Lecture lecture = new Lecture();
        User user = userRepository.findByEmail("chulsoo.prof@univ.com")
                .orElseThrow(() -> new RuntimeException());

        lecture.setName("생명");                     // 강의명
        lecture.setUser(user);                          // 교수 또는 담당자 정보 (User 엔티티)
        lecture.setCredit(2);                           // 학점
        lecture.setStartDate(LocalDate.of(2025, 7, 5));
        lecture.setDescription("생명 강의입니다.");  // 강의 설명

        lecture.setMajor(user.getMajor());              // 담당 전공
        lecture.setTotalStudent(40);                     // 총 수강생 수
        lecture.setStatus(Status.COMPLETED);             // 강의 상태 (예: COMPLETED, ONGOING 등)

        // 반드시 null 불가 컬럼인 completionDiv 설정 추가
        lecture.setCompletionDiv(CompletionDiv.MAJOR_REQUIRED);
        lectureRepository.save(lecture);                 // 강의 저장
    }

    @Test
    public void insertLectureBioTechnology() {
        Lecture lecture = new Lecture();
        User user = userRepository.findByEmail("qwe@qwe")
                .orElseThrow(() -> new RuntimeException());

        lecture.setName("생명공학");                     // 강의명
        lecture.setUser(user);                          // 교수 또는 담당자 정보 (User 엔티티)
        lecture.setCredit(3);                           // 학점
        lecture.setStartDate(LocalDate.of(2025, 3, 2));
        lecture.setEndDate(LocalDate.of(2025, 6, 18));
        lecture.setDescription("생명공학 강의입니다.");  // 강의 설명

        lecture.setMajor(user.getMajor());              // 담당 전공
        lecture.setTotalStudent(40);                     // 총 수강생 수
        lecture.setStatus(Status.COMPLETED);             // 강의 상태 (예: COMPLETED, ONGOING 등)

        // 반드시 null 불가 컬럼인 completionDiv 설정 추가
        lecture.setCompletionDiv(CompletionDiv.MAJOR_REQUIRED);
        lectureRepository.save(lecture);                 // 강의 저장
    }

    @Test
    public void insertLectureLifeScience() {
        Lecture lecture = new Lecture();
        User user = userRepository.findByEmail("qwe@qwe")
                .orElseThrow(() -> new RuntimeException());

        lecture.setName("삶과 과학");                     // 강의명
        lecture.setUser(user);                          // 교수 또는 담당자 정보 (User 엔티티)
        lecture.setCredit(2);                           // 학점
        lecture.setStartDate(LocalDate.of(2025, 3, 2));
        lecture.setEndDate(LocalDate.of(2025, 6, 18));
        lecture.setDescription("삶과 과학 강의입니다.");  // 강의 설명

        lecture.setMajor(user.getMajor());              // 담당 전공
        lecture.setTotalStudent(30);                     // 총 수강생 수
        lecture.setStatus(Status.INPROGRESS);             // 강의 상태 (예: COMPLETED, ONGOING 등)

        // 반드시 null 불가 컬럼인 completionDiv 설정 추가
        lecture.setCompletionDiv(CompletionDiv.LIBERAL_ELECTIVE);
        lectureRepository.save(lecture);                 // 강의 저장
    }


}
