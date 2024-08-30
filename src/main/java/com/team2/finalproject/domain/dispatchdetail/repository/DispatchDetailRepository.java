package com.team2.finalproject.domain.dispatchdetail.repository;

import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DispatchDetailRepository extends JpaRepository<DispatchDetail, Long> {

    @Query("select dd from DispatchDetail dd join fetch dd.transportOrder t join fetch t.center c where dd.id in :idList")
    List<DispatchDetail> findWithTransportOrderAndCenterByIdIn(List<Long> idList);

}
