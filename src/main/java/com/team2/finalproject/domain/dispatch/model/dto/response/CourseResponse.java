package com.team2.finalproject.domain.dispatch.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private boolean errorYn;  // 오류 여부
    private String smName;  // 기사 이름
    private String smPhoneNumber;  // 기사 전화번호
    private String tonCode;  // 차량 톤 코드
    private double ton;  // 차량 톤
    private int orderNum;  // 주문 수
    private int mileage;  // 주행 거리 (km)
    private int totalTime;  // 주행 시간 (분)
    private int floorAreaRatio;  // 용적률
    private List<CourseDetailResponse> courseDetailResponseList;  // 경로의 상세 정보 리스트
    private List<CoordinatesResponse> coordinatesResponseList;  // 경로의 좌표 리스트
}
