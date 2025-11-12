package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.LectureNotice;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureNoticeRepository extends JpaRepository<LectureNotice, Long> {
    List<LectureNotice> findNoticeByUser(User user);

    Page<LectureNotice> findByUser(User user, Pageable pageable);


    Page<LectureNotice> findByLecture(Lecture lecture, Pageable pageable);

    Page<LectureNotice> findByLectureIdIn(List<Long> lectures, Pageable pageable);
}
