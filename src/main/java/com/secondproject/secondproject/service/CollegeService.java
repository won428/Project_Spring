package com.secondproject.secondproject.service;

import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeRepository collegeRepository;


    public List<College> collegeList() {
        return this.collegeRepository.findAll();
    }
}
