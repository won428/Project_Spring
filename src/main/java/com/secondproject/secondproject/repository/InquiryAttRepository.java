package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Mapping.InquiryAttach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryAttRepository extends JpaRepository<InquiryAttach, Long> {
    List<InquiryAttach> findAllByInquiry_Id(long id);
}
