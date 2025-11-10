package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findAllByUser_Id(Long id);
}
