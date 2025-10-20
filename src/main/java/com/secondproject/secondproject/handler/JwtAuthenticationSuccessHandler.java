package com.secondproject.secondproject.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.config.JWT.JwtTokenProvider;
import com.secondproject.secondproject.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        String userEmail = authentication.getName();
        User user = (User) authentication.getPrincipal();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        //token 생성
        String token = jwtTokenProvider.createToken(userEmail, roles.toString());
        System.out.println(token);
        //user 이름 추가
        LoginResponseDto responseDto = new LoginResponseDto(token, user.getU_name());
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(responseDto); //Json 으로 변환 해줌

        response.getWriter().write(jsonResponse);

    }
}
