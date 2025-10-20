package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(Long userId);


    User findByEmail(String email);
}
