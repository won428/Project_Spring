package com.secondproject.secondproject.Test;

import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.util.List;

@SpringBootTest
public class EnrollmentInsert extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    public void insertEnrollmentFromExistingData() {

        Long userId = 1L; // 수강 데이터 생성해줄 학생 ID
        User user = userRepository.findById(userId).orElseThrow();

        // 성적 기록이 존재하는 과목 = 이미 수강 완료된 강의라고 간주
        List<Grade> grades = gradeRepository.findByUserId(userId);

        for (Grade grade : grades) {

            Lecture lecture = grade.getLecture(); // 성적과 연결된 강의 가져옴

            // 이미 enrollment 테이블에 해당 유저 + 강의 데이터가 존재하는지 확인
            boolean exists = enrollmentRepository.existsByUserAndLecture(user, lecture);
            if (exists) continue; // 중복 방지

            Enrollment enrollment = new Enrollment();
            enrollment.setUser(user);
            enrollment.setLecture(lecture);
            enrollment.setGrade(grade);

            // 종강 상태로 처리
            enrollment.setStatus(Status.COMPLETED);

            enrollmentRepository.save(enrollment);
        }
    }
}
