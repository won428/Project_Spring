package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findByUser(User user);



}
