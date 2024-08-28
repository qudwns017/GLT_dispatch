package com.team2.finalproject.domain.center.model.entity;

import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicledetail.model.entity.VehicleDetail;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Center extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String centerCode;  // 센터코드

    @Column(nullable = false, length = 50)
    private String centerName;  // 센터명

    @Column(nullable = false, length = 30)
    private String adminName; // 담당자명

    @Column(nullable = false, length = 7)
    private String zipCode;  // 우편번호

    @Column(nullable = false, length = 100)
    private String roadAddress;  // 도로명주소

    @Column(nullable = false, length = 100)
    private String customerAddress;  // 주소

    @Column(nullable = false, length = 50)
    private String detailAddress;  // 상세주소

    @Column(nullable = false)
    private Double latitude;  // 위도

    @Column(nullable = false)
    private Double longitude;  // 경도

    @Column(nullable = true, length = 10)
    private String restrictedTonCode;    // 진입 불가 톤 코드

    @Column(nullable = false, length = 20)
    private String phoneNumber; //전화번호

    @Builder.Default
    @Column(nullable = false)
    private int delayTime = 60; // 상차 추가 소요시간(분)

    @Column(nullable = true, length = 100)
    private String comment; // 비고

    @OneToMany(mappedBy = "center")
    List<DeliveryDestination> deliveryDestinationList;

    @OneToMany(mappedBy = "center")
    List<Users> usersList;

    @OneToMany(mappedBy = "center")
    List<Sm> smList;

    @OneToMany(mappedBy = "center")
    List<Vehicle> vehicleList;

    @OneToMany(mappedBy = "center")
    List<VehicleDetail> vehicleDetailList;

    @OneToMany(mappedBy = "center")
    List<DispatchNumber> dispatchNumberList;

    @OneToMany(mappedBy = "center")
    List<TransportOrder> transportOrderList;

}
