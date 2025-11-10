package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.dto.OnlineLectureDto;
import com.secondproject.secondproject.entity.Mapping.OnlineLectureAttach;
import com.secondproject.secondproject.entity.OnlineLecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnlineLectureAttachRepository extends JpaRepository<OnlineLectureAttach, Long> {
   

    OnlineLectureAttach findByOnlineLecture(OnlineLecture onlineLecture);
}
