package com.team2.finalproject.domain.users.repository;

import com.team2.finalproject.domain.users.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}
