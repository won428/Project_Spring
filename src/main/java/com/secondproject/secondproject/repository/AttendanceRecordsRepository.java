package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.dto.AttendanceResponseDto;
import com.secondproject.secondproject.entity.Attendance_records;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordsRepository extends JpaRepository<Attendance_records, Long> {

    // 수강ID + 일자 기준으로 단건 조회
    Optional<Attendance_records> findByEnrollmentIdAndAttendanceDate(Long enrollmentId, LocalDate attendanceDate);

    // 강의ID + 일자 기준 : 1건이라도 있으면 true
    boolean existsByEnrollment_Lecture_IdAndAttendanceDate(Long lectureId, LocalDate attendanceDate);

    // 강의ID + 일자 기준 : 총 저장된 출결 건수
    long countByEnrollment_Lecture_IdAndAttendanceDate(Long lectureId, LocalDate attendanceDate);

    List<Attendance_records> findByEnrollment_Lecture_IdAndAttendanceDate(Long lectureId, LocalDate attendanceDate);

    @Query("""
select
  count(a) as total,
  coalesce(sum(case when a.attendStudent = com.secondproject.secondproject.Enum.AttendStudent.PRESENT     then 1 else 0 end), 0) as present,
  coalesce(sum(case when a.attendStudent = com.secondproject.secondproject.Enum.AttendStudent.LATE        then 1 else 0 end), 0) as late,
  coalesce(sum(case when a.attendStudent = com.secondproject.secondproject.Enum.AttendStudent.EARLY_LEAVE then 1 else 0 end), 0) as earlyLeave,
  coalesce(sum(case when a.attendStudent = com.secondproject.secondproject.Enum.AttendStudent.ABSENT      then 1 else 0 end), 0) as absent,
  coalesce(sum(case when a.attendStudent = com.secondproject.secondproject.Enum.AttendStudent.EXCUSED     then 1 else 0 end), 0) as excused
from Attendance_records a
join a.enrollment e
where e.lecture.id = :lectureId
  and (:userId is null or a.user.id = :userId)
""")
    AttendanceCounts countByLectureAndOptionalUser(@Param("lectureId") Long lectureId,
                                                   @Param("userId") Long userId);

    List<Attendance_records> findByUserIdAndEnrollmentId(Long userId, Long enrollmentId);
}
