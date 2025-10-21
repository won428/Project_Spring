package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Entity.Lecture;
import com.secondproject.secondproject.Entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EnrollmentInfoDto {
    private Long studentId;
    private String studentName;
    private Long lectureId;
    private String lectureName;
    // 추가 필드 필요 시 확장 가능

    public EnrollmentInfoDto(User student, Lecture lecture) {
        this.studentId = student.getId(); // 학번
        this.studentName = student.getU_name(); // 학생 이름
        this.lectureId = lecture.getId(); // 강의 코드
        this.lectureName = lecture.getLec_name(); // 강의 이름
    }
}
