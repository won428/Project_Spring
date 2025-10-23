package com.secondproject.secondproject.Service;

import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private UserRepository userRepository;

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User setPasswordByEmail(String email) {
        return userRepository.setPasswordByEmail(email);
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.getByEmail(email);
    }


}
