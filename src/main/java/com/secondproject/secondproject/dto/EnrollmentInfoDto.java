package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EnrollmentInfoDto {
    private Long Id; // enroll_id
    private Long studentId; // userid가 학생인 경우의 필드
    private String studentName;
    private Long lectureId;
    private String lectureName;
    // 추가 필드 필요 시 확장 가능

    public EnrollmentInfoDto(User student, Lecture lecture, Enrollment enrollment) {
        this.Id = enrollment.getId();
        this.studentId = student.getId(); // 학번
        this.studentName = student.getName(); // 학생 이름
        this.lectureId = lecture.getId(); // 강의 코드
        this.lectureName = lecture.getName(); // 강의 이름
    }
}
