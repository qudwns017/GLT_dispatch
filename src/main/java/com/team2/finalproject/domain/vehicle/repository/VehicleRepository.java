package com.team2.finalproject.domain.vehicle.repository;

import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findVehicleBySm(Sm sm);

    default Vehicle findBySm(Sm sm) {
        return findVehicleBySm(sm).orElse(null);
    }

    Optional<Vehicle> findVehicleBySm_Id(Long smId);

    default Vehicle findBySm_Id(Long smId) {
        return findVehicleBySm_Id(smId).orElse(null);
    }

    @Query("SELECT SUM(v.maxLoadVolume) FROM Vehicle v WHERE v.sm.id IN :smIds")
    Double findTotalMaxLoadVolumeBySmIds(List<Long> smIds);

    @Query("SELECT SUM(v.maxLoadWeight) FROM Vehicle v WHERE v.sm.id IN :smIds")
    Double findTotalMaxLoadWeightBySmIds(List<Long> smIds);
}
