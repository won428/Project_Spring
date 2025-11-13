package com.secondproject.secondproject.service;

import com.secondproject.secondproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    //Spring 내장 Username은 String
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userCode = Long.parseLong(username);
        System.out.println(userCode);
        return userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

//    //test용
//    public static UserDetails loadUserByEmail(String userEmail) throws UsernameNotFoundException {
//        System.out.println("Email : " + userEmail);
//        if ("Admin123@Admin".equals(userEmail)) {
//            return org.springframework.security.core.userdetails.User.builder()
//                    .username("Admin123@Admin")
//                    .password(new BCryptPasswordEncoder().encode("Admin123"))
//                    .roles("ADMIN")
//                    .build();
//        }
//        throw new UsernameNotFoundException("계정 정보를 찾을 수 없습니다.");
//    }


}
