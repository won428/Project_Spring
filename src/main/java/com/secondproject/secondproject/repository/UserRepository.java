package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findById(Long id);


    Optional<User> findByEmail(String email);



    Optional<User> getByEmail(String email);
    Optional<User> findByUserCode(Long userCode);



    List<User> findAllByMajor(Major major);



    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}

