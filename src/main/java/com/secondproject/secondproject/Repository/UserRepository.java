package com.secondproject.secondproject.Repository;

import com.secondproject.secondproject.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
