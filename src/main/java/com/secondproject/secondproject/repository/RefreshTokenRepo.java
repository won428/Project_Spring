package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
//    Optional<RefreshToken> findByUsername(String username);

    void deleteByUsername(String username);

    Optional<RefreshToken> findByUsername(String username);
}
