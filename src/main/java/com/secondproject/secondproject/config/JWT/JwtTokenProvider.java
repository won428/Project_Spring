package com.secondproject.secondproject.config.JWT;

import com.secondproject.secondproject.service.MemberDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final String tokenKey = Base64.getEncoder().encodeToString("secretKey".getBytes());
    private final long validityInMs = 3600000; //1시간

    @Autowired
    private final MemberDetailService detailService;


    //JWT 토큰 생성
    public String createToken(String email, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(email); //JWT의 정보조각 Key-Value data <HEADER>.<PAYLOAD>.<SIGNATURE> 중 Payload
        claims.put("roles", roles);

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + validityInMs);//토큰 만료 시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.ES256, tokenKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(tokenKey).parseClaimsJwt(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

    }


    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (//"Authorization: Bearer <토큰>" 형태 확인
                bearer != null && bearer.startsWith("Bearer")) ?
                bearer.substring(7) ://"Bearer " 제거 JWT 토큰만 추출
                null;
    }

    public Authentication getAuthentication(String token) {
        String userEmail = getMemberName(token);
        UserDetails userDetails = MemberDetailService.loadUserByEmail(userEmail);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getMemberName(String token) {
        return Jwts.parser().setSigningKey(tokenKey).parseClaimsJwt(token).getBody().getSubject();
    }

}
