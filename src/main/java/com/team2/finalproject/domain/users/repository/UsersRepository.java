package com.team2.finalproject.domain.users.repository;

import com.team2.finalproject.domain.users.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
