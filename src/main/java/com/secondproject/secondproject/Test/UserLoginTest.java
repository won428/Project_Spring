package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserLoginTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertUserList() {
        User user01 = new User();
        user01.setEmail("Admin123@Admin");
        user01.setPassword(passwordEncoder.encode("Admin123"));
        user01.setGender("MALE");
        user01.setName("Kim");
        user01.setGender("FEMALE");
        user01.setPhone("01022223333");
        user01.setType(UserType.ADMIN);
        user01.setPhone("01012341234");
        userRepository.save(user01);
    }

    @Test
    public void insertProfessor_ChulSoo() {
        User prof = new User();
        prof.setEmail("chulsoo.prof@univ.com");        // 고유 이메일
        prof.setPassword(passwordEncoder.encode("Prof123!")); // 로그인용 원문 비번 예: Prof123!
        prof.setName("김철수");                           // 이름
        prof.setGender("MALE");                         // 성별 (스키마/엔티티에 맞는 값)
        prof.setPhone("01012345678");                  // 연락처
        prof.setType(UserType.PROFESSOR);
        userRepository.save(prof);// 교수 권한
    }
}
