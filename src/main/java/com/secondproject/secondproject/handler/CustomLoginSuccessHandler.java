package com.secondproject.secondproject.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.config.JWT.JwtTokenProvider;
import com.secondproject.secondproject.repository.UserRepository;
import com.secondproject.secondproject.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    //    UserService
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 1. Principal에서 인증된 User 객체를 직접 가져옵니다.
        //    이것만으로 충분하며, DB를 다시 조회할 필요가 없습니다!
        User user = (User) authentication.getPrincipal();

        // 2. JWT 토큰을 생성합니다.
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getU_type().name());

        // 3. 응답을 위한 깔끔한 DTO를 생성합니다. (Map 대신 DTO 사용 권장)
        LoginResponseDto responseDto = new LoginResponseDto(token, user.getU_name());

        // 4. 응답 헤더를 설정하고, DTO를 JSON으로 변환하여 전송합니다.
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(responseDto);
        response.getWriter().write(jsonResponse);
    }
}
