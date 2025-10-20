package com.secondproject.secondproject.config.JWT;

import com.secondproject.secondproject.service.UserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final String tokenKey = Base64.getEncoder().encodeToString("secretKey".getBytes());
    private final long validityInMs = 3600000; //1시간
    @Value("{app.jwt.secret")
    private String secret;
    @Value("{app.jwt.access-exp-min")
    private long accessExpMin;

    @Value("{app.jwt.refresh-exp-days")
    private long refreshExpDays;

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    //JWT 토큰 생성
    public String createToken(String email, Collection<String> roles) {
        Claims claims = Jwts.claims().setSubject(email); //JWT의 정보조각 Key-Value data <HEADER>.<PAYLOAD>.<SIGNATURE> 중 Payload
        claims.put("roles", roles); //Token에 User roles 추가

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + validityInMs);//토큰 만료 시간

        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, tokenKey)
                .compact();
    }

    //  토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

    }

    //토큰 해체
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (//"Authorization: Bearer <토큰>" 형태 확인
                bearer != null && bearer.startsWith("Bearer")) ?
                bearer.substring(7) ://"Bearer " 제거 JWT 토큰만 추출
                null;
    }

    //토큰 내 정보 로딩
    public Authentication getAuthentication(String token) {
        String userEmail = getUserEmail(token);
        UserDetails userDetails = detailService.loadUserByUsername(userEmail);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        return Jwts.parser().setSigningKey(tokenKey).parseClaimsJwt(token).getBody().getSubject();
    }

}
