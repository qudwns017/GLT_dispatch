package com.team2.finalproject.domain.users.repository;

import com.team2.finalproject.domain.users.exception.UsersErrorCode;
import com.team2.finalproject.domain.users.exception.UsersException;
import com.team2.finalproject.domain.users.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    default Users findByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new UsersException(UsersErrorCode.NOT_FOUND_USER));
    }

    default Users findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new UsersException(UsersErrorCode.NOT_FOUND_USER));
    }

    boolean existsByUsername(String username);
    void deleteByUsername(String username);

    // name을 기준으로 id 조회
    Optional<Users> findByName(String name);

    // name을 기준으로 id 조회하고, 존재하지 않을 경우 null 반환
    default Users findByNameOrNull(String name) {
        return findByName(name)
                .orElse(null);
    }
}
