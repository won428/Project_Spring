package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findById(Long id);


    Optional<User> findByEmail(String email);


    Optional<User> getByEmail(String email);

    Optional<User> findByUserCode(Long userCode);


    List<User> findAllByMajor(Major major);

    // 이메일 존재하는지 확인
    boolean existsByEmail(String email);

    // DB에 존재하는 모든 이메일 리스트로 받아오기
    List<User> findAllByEmailIn(Collection<String> emails);


    boolean existsByPhone(String phone);

    Optional<User> getByUserCode(Long usercode);

    List<User> findAllByMajor_IdAndType(Long id, UserType userType);

    List<User> findAllByMajor_id(Long id);
}

