package com.team2.finalproject.domain.center.model.entity;

import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Center extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String field;  // 필드

    @Column(nullable = false, length = 50)
    private String centerName;  // 센터명

    @Column(nullable = false, length = 7)
    private String postalCode;  // 우편번호

    @Column(nullable = false, length = 100)
    private String address;  // 주소

    @Column(nullable = false)
    private Double latitude;  // 위도

    @Column(nullable = false)
    private Double longitude;  // 경도

    @OneToMany(mappedBy = "center")
    private List<Sm> smList;

    @OneToMany(mappedBy = "center")
    private List<DeliveryDestination> deliveryDestinationList;

    @OneToMany(mappedBy = "center")
    private List<Users> userList;

    @OneToMany(mappedBy = "center")
    private List<Vehicle> vehicleList;
}
