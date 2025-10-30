package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.Enum.Gender;
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
        user01.setEmail("test@test.com");
        user01.setPassword(passwordEncoder.encode("Test123"));
        user01.setGender(Gender.MALE);
        user01.setName("Choi");
        user01.setPhone("01033334444");
        user01.setType(UserType.ADMIN);
        userRepository.save(user01);
    }

}
