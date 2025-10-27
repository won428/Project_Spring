package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Notice;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Notice, Long> {
    List<Notice> findNoticeByUser(User user);
}
