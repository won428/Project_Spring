package com.secondproject.secondproject.Test;


import com.secondproject.secondproject.Enum.Gender;
import com.secondproject.secondproject.entity.Major;
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
        user01.setEmail("test@test");
        user01.setPassword(passwordEncoder.encode("1234"));
        user01.setGender(Gender.MALE);
        user01.setName("admin");
        user01.setPhone("01012378453");
        user01.setType(UserType.ADMIN);
        userRepository.save(user01);
    }

    @Test
    public void insertProfessor_ChulSoo() {
        Major major = new Major();
        major.setId(5001L); // 이 숫자가 DB FK로 들어감

        User prof = new User();
        prof.setEmail("chulsoo.prof@univ.com");        // 고유 이메일
        prof.setPassword(passwordEncoder.encode("Prof123!")); // 로그인용 원문 비번 예: Prof123!
        prof.setName("김철수");                           // 이름
        prof.setGender(Gender.valueOf("MALE"));                         // 성별 (스키마/엔티티에 맞는 값)
        prof.setPhone("01012345678");                  // 연락처
        prof.setType(UserType.PROFESSOR);
        userRepository.save(prof);// 교수 권한

        prof.setPhone("01012347777");                               // UNIQUE, length 20
        prof.setType(UserType.STUDENT);                             // Enum: 학생
        prof.setUserCode(202500051002L);
        prof.setMajor(major);
    }

    @Test
    public void insertUser_김영희() {
        User std1 = new User(); // userid: 1
        std1.setEmail("younghee.kim@example.com");        // UNIQUE, length 100
        std1.setPassword(passwordEncoder.encode("Younghee123!")); // 인코딩된 비밀번호
        std1.setName("김영희");                              // u_name NOT NULL
        std1.setGender(Gender.valueOf("FEMALE"));                          // 문자열 컬럼
        std1.setPhone("01077778888");                      // UNIQUE, length 20
        std1.setType(UserType.STUDENT);                    // Enum: 학생
        userRepository.save(std1);
    }

    @Test
    public void insertUser_김명수() {
        // given
        User std2 = new User(); // userid: 2
        std2.setEmail("myungsoo.kim@example.com");                  // UNIQUE, length 100
        std2.setPassword(passwordEncoder.encode("Myungsoo123!"));   // 인코딩된 비밀번호
        std2.setName("김명수");                                       // u_name NOT NULL
        std2.setGender(Gender.valueOf("MALE"));                                     // 문자열 컬럼(예: 'MALE' / 'FEMALE')
        std2.setPhone("01066667777");                               // UNIQUE, length 20
        std2.setType(UserType.STUDENT);                             // Enum: 학생

        // when
        userRepository.save(std2);
    }

    @Test
    public void insertProfessor_John() {
        User prof = new User();
        prof.setEmail("John.prof@univ.com");        // 고유 이메일
        prof.setPassword(passwordEncoder.encode("John123!")); // 로그인용 원문 비번 예: Prof123!
        prof.setName("John");                           // 이름
        prof.setGender(Gender.valueOf("MALE"));                         // 성별 (스키마/엔티티에 맞는 값)
        prof.setPhone("01012345456");                  // 연락처
        prof.setType(UserType.PROFESSOR);
        userRepository.save(prof);// 교수 권한
    }

    @Test
    public void insertUser_TOM() {
        // Major 객체 생성 (ID만 세팅)
        Major major = new Major();
        major.setId(5001L); // 이 숫자가 DB FK로 들어감
        // given
        User std3 = new User(); // userid: 2
        std3.setEmail("tom@example.com");                  // UNIQUE, length 100
        std3.setPassword(passwordEncoder.encode("tom123"));   // 인코딩된 비밀번호
        std3.setName("TOM");                                       // u_name NOT NULL
        std3.setGender(Gender.valueOf("MALE"));                                     // 문자열 컬럼(예: 'MALE' / 'FEMALE')
        std3.setPhone("01012347777");                               // UNIQUE, length 20
        std3.setType(UserType.STUDENT);                             // Enum: 학생
        std3.setUserCode(202500051002L);
        std3.setMajor(major);


        // when
        userRepository.save(std3);
    }
}
