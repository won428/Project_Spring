package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Entity.RefreshToken;
import com.secondproject.secondproject.config.JWT.JwtTokenProvider;
import com.secondproject.secondproject.repository.RefreshTokenRepo;
import com.secondproject.secondproject.service.AuthService;
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
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;


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
            com.secondproject.secondproject.entity.User user = (com.secondproject.secondproject.entity.User) authentication.getPrincipal();

            String role = user.getType().name();
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

//    Pw 찾기 기능 진행중 ...
//    @PostMapping("/FindPW")
//    public ResponseEntity<?> findPw(@RequestBody FindRequest findRequest) {
//        String userEmail = findRequest.email;
//        Optional<User> authUser = authService.findUserByEmail(userEmail);
//        if (authUser.isPresent()) {
//            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email 검증 성공");
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("검증 실패");
//        }
//    }


//    @PostMapping("/setPw/:{email}")
//    public ResponseEntity<?> setPw(@PathVariable String email, @RequestBody PwSetRequest pwsetRequest) {
//        Optional<User> authUser = authService.getByEmail(email);
//        String newPassword = pwsetRequest.newPassword;
//        if(authUser.isEmpty()){
//            return ResponseEntity.notFound().build();
//        }else {
//            User user = authUser.get();
//            user.setPassword(newPassword);
//        }
//
//
//
//    }

// react 에서 access token 만료로 구현
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestBody LoginRequest loginRequest) {
//        refreshTokenRepo.deleteByEmail(loginRequest.getEmail());
//        return ResponseEntity.ok("로그아웃 완료");
//    }

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
    public static class FindRequest {
        private String email;
    }

    @Data
    public static class PwSetRequest {
        private String newPassword;
    }

    @Data
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }

}
