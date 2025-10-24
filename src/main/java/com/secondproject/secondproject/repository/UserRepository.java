package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);


    Optional<User> findByEmail(String email);



    Optional<User> getByEmail(String email);
    Optional<User> findByUserCode(Long userCode);



    List<User> findAllByMajor(Major major);
}

