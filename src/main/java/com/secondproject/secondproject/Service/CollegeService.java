package com.secondproject.secondproject.Service;

import com.secondproject.secondproject.Entity.College;
import com.secondproject.secondproject.Repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeRepository collegeRepository;

    public List<College> getCollegeList() {
        return this.collegeRepository.findAll();
    }
}
