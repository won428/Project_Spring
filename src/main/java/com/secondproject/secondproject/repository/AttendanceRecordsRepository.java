package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Attendance_records;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRecordsRepository extends JpaRepository<Attendance_records, Long> {

    // 수강ID + 일자 기준으로 단건 조회
    Optional<Attendance_records> findByEnrollmentIdAndAttendanceDate(Long enrollmentId, LocalDate attendanceDate);

    // 강의ID + 일자 기준 : 1건이라도 있으면 true
    boolean existsByEnrollment_Lecture_IdAndAttendanceDate(Long lectureId, LocalDate attendanceDate);

    // 강의ID + 일자 기준 : 총 저장된 출결 건수
    long countByEnrollment_Lecture_IdAndAttendanceDate(Long lectureId, LocalDate attendanceDate);
}
