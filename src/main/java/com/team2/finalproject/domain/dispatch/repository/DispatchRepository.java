package com.team2.finalproject.domain.dispatch.repository;

import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

}
