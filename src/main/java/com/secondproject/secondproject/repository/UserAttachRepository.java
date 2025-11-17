package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Mapping.UserAttach;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAttachRepository extends JpaRepository<UserAttach, Long> {

    @Query("SELECT ua FROM UserAttach ua WHERE ua.user.id = :userId")
    Optional<UserAttach> findByUserId(@Param("userId") Long userId);
}
