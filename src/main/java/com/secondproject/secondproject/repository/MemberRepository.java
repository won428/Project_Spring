package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<User, Long> {
    User getUserById(Long userId);

    Optional<User> findByUid(Long userId);

    User findByEmail(String email);
}
