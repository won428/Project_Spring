package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {
    private final MemberService memberService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberService.findByEmail(email);

        if (member == null) {
            String msg = "Id/Pw Error";
            throw new UsernameNotFoundException(msg);
        }


        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getU_type().name())
                .build();
    }

    public static UserDetails loadUserByEmail(String userEmail) throws UsernameNotFoundException {
        if ("testEmail".equals(userEmail)) {
            return User.builder()
                    .username("testEmail")
                    .password(new BCryptPasswordEncoder().encode("1234"))
                    .roles("USER")
                    .build();
        }
        throw new UsernameNotFoundException("계정 정보를 찾을 수 없습니다.");
    }


}
