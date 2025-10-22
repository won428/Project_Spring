package com.secondproject.secondproject.Repository;

import com.secondproject.secondproject.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 사용자 데이터를 조회, 저장, 삭제, 수정 등 데이터베이스 조작을 담당
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserById(Long userId);

    // 기존 findByUid 메서드는 삭제 혹은 미사용 처리
}
