package com.team2.finalproject.domain.vehicle.repository;

import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
