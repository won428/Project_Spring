package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.controller.CreditAppealController;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppealRepository extends JpaRepository<Appeal, Long> {


    List<Appeal> findBySendingId(Long sendingId);
    // lectureId AND receiverId 기준 조회
    List<Appeal> findByLecture_IdAndReceiverId(Long lectureId, Long receiverId);
    // lecture 기준 조회
    List<Appeal> findByLecture_Id(Long lectureId);

    // enrollment 기준 조회
    List<Appeal> findByEnrollment_Id(Long enrollmentId);
}
