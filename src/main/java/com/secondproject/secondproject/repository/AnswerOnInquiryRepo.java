package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.AnswerOnInquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerOnInquiryRepo extends JpaRepository<AnswerOnInquiry, Long> {
    List<AnswerOnInquiry> findAllByInquiry_id(Long id);
}
