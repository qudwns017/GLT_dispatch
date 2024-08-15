package com.team2.finalproject.domain.users.repository;

import com.team2.finalproject.domain.users.exception.UsersErrorCode;
import com.team2.finalproject.domain.users.exception.UsersException;
import com.team2.finalproject.domain.users.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    default Users findByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new UsersException(UsersErrorCode.NOT_FOUND_USER));
    }
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}
