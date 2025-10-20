package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserService userService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(email);
        User user = userService.findByEmail(email);

        if (user == null) {
            String msg = "Id/Pw Error";
            throw new UsernameNotFoundException(msg);
        }


        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getU_type().name())
                .build();
    }

    //test용
    public static UserDetails loadUserByEmail(String userEmail) throws UsernameNotFoundException {
        System.out.println("Email : " + userEmail);
        if ("Admin123@Admin".equals(userEmail)) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username("Admin123@Admin")
                    .password(new BCryptPasswordEncoder().encode("Admin123"))
                    .roles("ADMIN")
                    .build();
        }
        throw new UsernameNotFoundException("계정 정보를 찾을 수 없습니다.");
    }


}
