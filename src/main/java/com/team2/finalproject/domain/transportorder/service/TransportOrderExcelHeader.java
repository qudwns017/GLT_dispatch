package com.team2.finalproject.domain.transportorder.service;

import java.util.ArrayList;
import java.util.List;

public record TransportOrderExcelHeader(String name, String required, String comment, String example) {

    public static List<TransportOrderExcelHeader> getTransportOrders() {
        List<TransportOrderExcelHeader> transportOrders = new ArrayList<>();
        transportOrders.add(new TransportOrderExcelHeader(
                "배송유형 (지입/용차/택배)", "필수",
                "배송유형은 지입/용차/택배로 구분하여 입력해주세요.",
                "예시) 지입"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "SM명", "필수",
                "특정 주문을 작업할 담당 드라이버를 지정해주세요.\nGLT 코리아에 등록되지 않은 드라이버의 이름을 입력하면 오류가 발생합니다.",
                "예시) 이서원"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "운송장번호", "필수",
                "주문번호에 할당된 운송장번호를 입력해주세요.",
                "예시) C0029384889"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "업체주문번호", "선택",
                "운송장 번호가 할당된 주문번호를 입력해주세요.\n관리의 편리함을 위해 있는 입력칸으로,\n필수값이 아니며 임의로 작성해주셔도 됩니다.",
                "예시) 240812_공동구매"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "주문유형", "선택",
                "배송/수거",
                "예시) 수거"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "주문접수일", "필수",
                "주문이 귀사에 접수된 날짜를 양식에 맞게 입력해주세요.\n미래의 날짜는 입력할 수 없습니다.",
                "예시) YYYYMMDD"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "작업희망일", "필수",
                "작업을 희망하는 날짜를 양식에 맞게 입력해주세요.",
                "예시) YYYYMMDD"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "희망도착시간", "필수",
                "희망 도착시간을 양식에 맞게 입력해 주세요.\n24시간제를 기준으로 작성 해주세요.",
                "예시) 15:30"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "고객명", "필수",
                "고객님의 성함 또는 상호를 입력해주세요.",
                "예시) 홍길동"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "고객연락처", "필수",
                "고객님의 연락처를 입력해주세요.",
                "예시) 0212345678, 01000000000"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "주소", "필수",
                "도로명 주소와 건물번호를 입력해주세요.\n주소정보누리집(www.juso.go.kr) 양식을 따릅니다.",
                "예시) 테헤란로 11길 22"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "상세주소", "필수",
                "상세 주소를 입력해주세요.",
                "예시) 101동 203호"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "우편번호", "필수",
                "배송처의 우편번호를 입력해주세요.\n주소정보누리집(www.juso.go.kr) 양식을 따릅니다.",
                "예시) 06232"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "볼륨", "필수",
                "볼륨을 숫자 형태로 입력해주세요.\n*가로 x 세로 x 높이 값을 m^3을 기준으로 작성해주세요.",
                "예시) 600"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "중량", "필수",
                "중량을 정수 형태로 입력해주세요.\n*kg을 기준으로 작성해주세요.",
                "예시) 50"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "고객전달사항", "선택",
                "고객전달사항을 입력해주세요.",
                ""
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "예상작업시간", "선택",
                "도착 후 작업 예상 작업 시간(분) 정수 형태로 입력해주세요.\n작업소요시간 셀이 비어있으면 기본값으로 1을 인식합니다.",
                "예시) 30"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "상품명", "필수",
                "상품명을 모두 입력해주세요.\n상품이 여러개인 경우 운송장번호를 동일하게 사용하여 다음줄에 입력해주세요.",
                "예시) 스탠리텀블러50"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "상품 코드", "선택",
                "귀사에서 사용 중인 상품코드를 입력해주세요.",
                "예시) st05"
        ));
        transportOrders.add(new TransportOrderExcelHeader(
                "상품 수량", "필수",
                "상품의 수량을 정수 형태로 입력해주세요.\n상품수량 셀이 비어있으면 기본값으로 1을 인식합니다.",
                "예시) 5"
        ));

        return transportOrders;
    }
}
