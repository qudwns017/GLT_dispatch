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

    // id를 기준으로 name 조회
    @Query("SELECT u.name FROM Users u WHERE u.id = :id")
    Optional<String> findNameById(@Param("id") Long id);

    // id를 기준으로 name 조회하고, 존재하지 않을 경우 예외 던지기
    default String findNameByIdOrThrow(Long id) {
        return findNameById(id)
                .orElseThrow(() -> new UsersException(UsersErrorCode.NOT_FOUND_USER));
    }
}
