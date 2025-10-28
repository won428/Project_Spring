package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.CollegeRepository;
import com.secondproject.secondproject.repository.MajorRepository;
import com.secondproject.secondproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.LocalDate;

@SpringBootTest
public class MajorInsert extends AbstractTestNGSpringContextTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;
    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertCollegeList() {
        College college = new College();
        college.setType("New");
        collegeRepository.save(college);

    }

    @Test
    public void insertMajorList() {
        Major major = new Major();
        major.setName("Physics");
        major.setCollege(major.getCollege());
        majorRepository.save(major);

    }


}
