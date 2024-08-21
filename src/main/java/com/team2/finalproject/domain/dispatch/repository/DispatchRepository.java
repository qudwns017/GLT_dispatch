package com.team2.finalproject.domain.dispatch.repository;

import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
    // 배차번호Id 리스트를 기준으로 Dispatch 리스트 조회
    List<Dispatch> findByDispatchNumberIdIn(List<Long> dispatchNumberIds);
}
