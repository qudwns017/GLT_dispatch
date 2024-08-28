package com.team2.finalproject.domain.vehicledetail.repository;

import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicledetail.model.entity.VehicleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleDetailRepository extends JpaRepository<VehicleDetail, Long> {
    Optional<VehicleDetail> findVehicleDetailByVehicle(Vehicle vehicle);

    default VehicleDetail findByVehicle(Vehicle vehicle) {
        return findVehicleDetailByVehicle(vehicle).orElse(null);
    }
}
