package com.secondproject.secondproject.config.JWT;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-exp}")
    private long accessExp;
    @Value("${jwt.refresh-exp}")
    private long refreshExp;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long id, String username, String role, String uname) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim("uid", String.valueOf(id))
                .claim("role", role)
                .claim("uname", uname)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long id, String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim("uid", String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

//    public String getEmail(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }


    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}