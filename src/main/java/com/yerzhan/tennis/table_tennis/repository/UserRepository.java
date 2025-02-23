package com.yerzhan.tennis.table_tennis.repository;

import com.yerzhan.tennis.table_tennis.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {
    Optional<Users> findByUsername(String username);
}
