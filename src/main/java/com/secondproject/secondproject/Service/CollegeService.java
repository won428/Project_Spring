package com.secondproject.secondproject.Service;

import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
