package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member getUserById(Long userId);

    Optional<Member> findByUid(Long userId);

    Member findByEmail(String email);
}
