package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.controller.CreditAppealController;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Attendance_records;
import com.secondproject.secondproject.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AppealRepository extends JpaRepository<Appeal, Long> {


    List<Appeal> findBySendingId(Long sendingId);
    // lectureId AND receiverId 기준 조회
    @Query("SELECT a FROM Appeal a " +
            "LEFT JOIN FETCH a.attendanceRecord ar " +
            "WHERE a.lecture.id = :lectureId AND a.receiverId = :receiverId")
    List<Appeal> findByLecture_IdAndReceiverIdWithAttendance(@Param("lectureId") Long lectureId,
                                                             @Param("receiverId") Long receiverId);
    // lecture 기준 조회
    List<Appeal> findByLecture_Id(Long lectureId);

    // enrollment 기준 조회
    List<Appeal> findByEnrollment_Id(Long enrollmentId);

    @Query("SELECT a FROM Appeal a " +
            "LEFT JOIN FETCH a.attendanceRecord ar " +
            "LEFT JOIN FETCH a.enrollment e " +
            "LEFT JOIN FETCH e.user u " +
            "LEFT JOIN FETCH e.grade g " +
            "WHERE a.lecture.id = :lectureId AND a.receiverId = :receiverId")
    List<Appeal> findByLectureAndReceiverWithDetails(
            @Param("lectureId") Long lectureId,
            @Param("receiverId") Long receiverId);

    @Query("SELECT a FROM Appeal a " +
            "WHERE a.lecture.id = :lectureId " +
            "AND a.sendingId = :studentId " +
            "AND a.appealType = 'ATTENDANCE' " +
            "ORDER BY a.appealDate DESC")
    Optional<Appeal> findTopByLectureIdAndSendingIdOrderByAppealDateDesc(
            @Param("lectureId") Long lectureId,
            @Param("studentId") Long studentId
    );
}
