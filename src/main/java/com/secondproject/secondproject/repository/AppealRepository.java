package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.controller.CreditAppealController;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppealRepository extends JpaRepository<Appeal, Long> {


    List<Appeal> findBySendingId(Long sendingId);
    List<Appeal> findByLectureIdAndReceiverId(Long lectureId, Long receiverId);
}
