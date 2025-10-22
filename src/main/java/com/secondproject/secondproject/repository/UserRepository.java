package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {


}
