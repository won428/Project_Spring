package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.entity.LectureNotice;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.CollegeRepository;
import com.secondproject.secondproject.repository.LectureNoticeRepository;
import com.secondproject.secondproject.repository.MajorRepository;
import com.secondproject.secondproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
public class NoticeInsert extends AbstractTestNGSpringContextTests {
    @Autowired
    private LectureNoticeRepository lectureNoticeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void insertCollegeList() {
        User user = userRepository.findById(304L).orElseThrow(() -> new EntityNotFoundException("dd"));

        for (int i = 100; i < 200; i++) {
            LectureNotice lec = new LectureNotice();
            int a = 10 * i + i;
            String msg = String.format("%d", a);
            lec.setUser(user);
            lec.setLnTitle(msg);
            lec.setLnContent(msg);

            lectureNoticeRepository.save(lec);
        }
    }


}
