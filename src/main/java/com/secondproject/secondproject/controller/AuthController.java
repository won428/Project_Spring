package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Entity.RefreshToken;
import com.secondproject.secondproject.config.JWT.JwtTokenProvider;
import com.secondproject.secondproject.repository.RefreshTokenRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

            String email = loginRequest.getEmail();
            System.out.println("로그인 이메일 : " + email);
            String password = loginRequest.getPassword();
            System.out.println("로그인 패스워드 : " + password);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            com.secondproject.secondproject.Entity.User user = (com.secondproject.secondproject.Entity.User) authentication.getPrincipal();

            String role = user.getU_type().name();
            System.out.println("역할  :" + role);
            String access = jwtTokenProvider.createAccessToken(email, role);
            String refresh = jwtTokenProvider.createRefreshToken(email);

            refreshTokenRepo.findByEmail(email)
                    .ifPresentOrElse(
                            r -> {
                                r.setToken(refresh);
                                refreshTokenRepo.save(r);
                            },
                            () -> refreshTokenRepo.save(new RefreshToken(null, email, refresh))

                    );

            return ResponseEntity.ok(new TokenResponse(access, refresh));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Id 또는 Pw가 잘못되었습니다.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {

        String email = refreshRequest.getEmail();


        Optional<RefreshToken> saved = refreshTokenRepo.findByEmail(email);
        {
            if (saved.isPresent()
                    && saved.get().getToken().equals(refreshRequest.getRefreshToken())
                    && jwtTokenProvider.validateToken(refreshRequest.getRefreshToken())
            ) {
                String newAccess = jwtTokenProvider.createAccessToken(email, "");
                return ResponseEntity.ok(new TokenResponse(newAccess, refreshRequest.getRefreshToken()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token 만료 / 불 일치");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LoginRequest loginRequest) {
        refreshTokenRepo.deleteByEmail(loginRequest.getEmail());
        return ResponseEntity.ok("로그아웃 완료");
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RefreshRequest {
        private String email;
        private String refreshToken;
    }

    @Data
    public static class LogoutRequest {
        private String email;
    }

    @Data
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }

}
