package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.PasswordChangeReqDto;
import com.secondproject.secondproject.entity.RefreshToken;
import com.secondproject.secondproject.config.JWT.JwtTokenProvider;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.RefreshTokenRepo;
import com.secondproject.secondproject.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

            String username = loginRequest.getUsername();
            System.out.println("로그인 학번 : " + username);
            String password = loginRequest.getPassword();
            System.out.println("로그인 패스워드 : " + password);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            com.secondproject.secondproject.entity.User user = (com.secondproject.secondproject.entity.User) authentication.getPrincipal();

            Long id = user.getId();
            String role = user.getType().name();
            String uname = user.getName();
            System.out.println("역할  :" + role);
            String access = jwtTokenProvider.createAccessToken(id, username, role, uname);
            String refresh = jwtTokenProvider.createRefreshToken(id, username);

            refreshTokenRepo.findByUsername(username)
                    .ifPresentOrElse(
                            r -> {
                                r.setToken(refresh);
                                refreshTokenRepo.save(r);
                            },
                            () -> refreshTokenRepo.save(new RefreshToken(null, username, refresh))

                    );

            return ResponseEntity.ok(new TokenResponse(access, refresh));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Id 또는 Pw가 잘못되었습니다.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {

        String username = refreshRequest.getUsername();
        Optional<RefreshToken> saved = refreshTokenRepo.findByUsername(username);
        {
            if (saved.isPresent()
                    && saved.get().getToken().equals(refreshRequest.getRefreshToken())
                    && jwtTokenProvider.validateToken(refreshRequest.getRefreshToken())
            ) {
                User user = userService.getByUserCode(Long.parseLong(username))
                        .orElseThrow(() -> new BadCredentialsException("사용자 없음"));
                Long userId = user.getId();
                String role = user.getType().name();
                String uname = user.getName();
                String newAccess = jwtTokenProvider.createAccessToken(userId, username, role, uname);
                return ResponseEntity.ok(new TokenResponse(newAccess, refreshRequest.getRefreshToken()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token 만료 / 불 일치");
        }
    }

    @PostMapping("/FindPW")
    public ResponseEntity<?> findPw(@RequestBody FindRequest findRequest) {
        String StringUsername = findRequest.getUsername();
        Long username = Long.parseLong(StringUsername);
        Optional<User> authUser = userService.findByUsername(username);
        if (authUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email 검증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("검증 실패");
        }
    }


    @PostMapping("/SetPw")
    public ResponseEntity<?> setPw(@Valid @RequestBody PasswordChangeReqDto pwSetRequest) {
        System.out.println("Request : " + pwSetRequest);
        String StringUsername = pwSetRequest.getUsername();
        Long username = Long.parseLong(StringUsername);
        Optional<User> authUser = userService.getByUserCode(username);
        if (authUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }


        String newPassword = (pwSetRequest.getNewPassword());
        String Password = authUser.get().getPassword();
        System.out.println(!passwordEncoder.matches(newPassword, Password));
        try {
            if (!passwordEncoder.matches(newPassword, Password)) {
                String encodedPassword = passwordEncoder.encode(pwSetRequest.getNewPassword());
                User user = authUser.get();
                user.setPassword(encodedPassword);
                userService.setPassword(user);
                return ResponseEntity.ok(HttpStatus.ACCEPTED);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("이전에 사용하는 Pw는 사용할 수 없습니다.");

            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    // react 에서 access token 만료로 구현
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LoginRequest loginRequest) {
        refreshTokenRepo.deleteByUsername(loginRequest.getUsername());
        return ResponseEntity.ok("로그아웃 완료");
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class RefreshRequest {
        private String username;
        private String refreshToken;
    }

    @Data
    public static class FindRequest {
        private String username;

    }


    @Data
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }


//    //비밀번호 제약 오류
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(
//            MethodArgumentNotValidException ex) {
//
//        Map<String, String> errors = new HashMap<>();
//
//        // 1. DTO의 '필드' 오류 (예: @Pattern, @NotBlank)
//        ex.getBindingResult().getFieldErrors().forEach(error -> {
//            String fieldName = error.getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        // 2. DTO의 '클래스 레벨' 오류 (예: @AssertTrue - 비밀번호 일치)
//        ex.getBindingResult().getGlobalErrors().forEach(error -> {
//            String objectName = error.getObjectName();
//            String errorMessage = error.getDefaultMessage();
//            errors.put("passwordConfirmError", errorMessage); // 키 이름은 프론트와 협의
//        });
//
//        return ResponseEntity.badRequest().body(errors);
//    }

}