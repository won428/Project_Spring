package com.secondproject.secondproject.Repository;

import com.secondproject.secondproject.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
//    Optional<RefreshToken> findByUsername(String username);

    Optional<RefreshToken> findByEmail(String email);

    void deleteByEmail(String email);
}
