package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.Entity.User;
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
        user01.setEmail("Admin123@Admin123");
        user01.setPassword(passwordEncoder.encode("Admin123"));
        user01.setU_name("Kim");
        user01.setU_type(UserType.ADMIN);
        userRepository.save(user01);

        User user02 = new User();
        user02.setEmail("Student123@Student123");
        user02.setPassword(passwordEncoder.encode("Student123"));
        user02.setU_name("Kim");
        user02.setU_type(UserType.STUDENT);
        userRepository.save(user02);

        User user03 = new User();
        user03.setEmail("PRO123@PRO123");
        user03.setPassword(passwordEncoder.encode("PRO123"));
        user03.setU_name("Kim");
        user03.setU_type(UserType.PROFESSOR);
        userRepository.save(user03);
    }

}
