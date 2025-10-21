package com.secondproject.secondproject.Service;

import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void insertUser(User user) {


        this.userRepository.save(user);
    }
}
