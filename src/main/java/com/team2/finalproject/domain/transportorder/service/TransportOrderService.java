package com.team2.finalproject.domain.transportorder.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.dto.response.CourseDetailResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.CourseResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.StartStopoverResponse;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.dto.request.OrderRequest;
import com.team2.finalproject.domain.transportorder.model.dto.request.SmNameAndZipCodeRequest;
import com.team2.finalproject.domain.transportorder.model.dto.request.TransportOrderRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndZipCodeResponse;
import com.team2.finalproject.domain.transportorder.model.dto.response.TransportOrderResponse;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.transportorder.repository.TransportOrderRepository;
import com.team2.finalproject.domain.users.repository.UsersRepository;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicle.repository.VehicleRepository;
import com.team2.finalproject.global.service.KakaoApiService;
import com.team2.finalproject.global.service.OptimizationService;
import com.team2.finalproject.global.util.request.OptimizationRequest;
import com.team2.finalproject.global.util.request.Stopover;
import com.team2.finalproject.global.util.response.AddressInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransportOrderService {

    private final CenterRepository centerRepository;
    private final DeliveryDestinationRepository deliveryDestinationRepository;
    private final DispatchNumberRepository dispatchNumberRepository;
    private final SmRepository smRepository;
    private final UsersRepository usersRepository;
    private final VehicleRepository vehicleRepository;
    private final KakaoApiService kakaoApiService;
    private final OptimizationService optimizationService;
    private final TransportOrderRepository transportOrderRepository;


    private static final short FONT_SIZE_NAME = 14;
    private static final short FONT_SIZE_REQUIRED = 12;
    private static final short COMMENT_ROW_HEIGHT = 2000;
    private static final int COLUMN_PADDING = 8192;

    @Transactional
    public DispatchResponse processTransportOrder(TransportOrderRequest request, Long userId) {
        // Center 조회
        Long centerId = usersRepository.findById(userId).orElseThrow().getCenter().getId();
        Center center = centerRepository.findById(centerId).orElseThrow();

        Stopover startStopover = Stopover.builder()
                .address(center.getRoadAddress() + " " + center.getDetailAddress())
                .lat(center.getLatitude())
                .lon(center.getLongitude())
                .delayTime(LocalTime.of(center.getDelayTime()/60,center.getDelayTime()%60,0))
                .build();

        // 지번 주소와 도로명 주소 간의 매핑을 위한 맵 생성
        Map<String, String[]> addressMapping = new HashMap<>();

        // smId와 해당 stopoverList를 매핑
        Map<Long, List<Stopover>> stopoversGroupedBySmId = request.orderReuquestList().stream()
                .collect(Collectors.groupingBy(
                        OrderRequest::smId,
                        Collectors.mapping(order -> {
                            // 도로명 주소로 입력받았으므로 도로명 주소로 있는지 조회
                            DeliveryDestination destination = deliveryDestinationRepository
                                    .findByFullAddress(order.address(), order.detailAddress());

                            if (destination != null) {
                                String roadAddress = destination.getRoadAddress() + " " + destination.getDetailAddress();
                                String[] addressArray = {destination.getCustomerAddress(), destination.getDetailAddress()};
                                addressMapping.put(roadAddress, addressArray);

                                return new Stopover(
                                        destination.getRoadAddress() + " " + destination.getDetailAddress(),
                                        destination.getLatitude(),
                                        destination.getLongitude(),
                                        LocalTime.of((destination.getDelayTime() + order.expectedServiceDuration()) / 60, (destination.getDelayTime() + order.expectedServiceDuration()) % 60, 0)
                                );
                            }

                            AddressInfo addressInfo = kakaoApiService.getAddressInfoFromKakaoAPI(order.address() + " " + order.detailAddress());

                            String roadAddress = order.address() + " " + order.detailAddress();
                            String[] addressArray = {addressInfo.getCustomerAddress(), order.detailAddress()};

                            // 맵에 지번 주소와 도로명 주소 매핑 추가
                            addressMapping.put(roadAddress, addressArray);
                            return new Stopover(
                                    order.address() + " " + order.detailAddress(),
                                    addressInfo.getLat(),
                                    addressInfo.getLon(),
                                    LocalTime.of(order.expectedServiceDuration() / 60, order.expectedServiceDuration() % 60, 0)
                            );
                        }, Collectors.toList())));

        // smId 순서를 저장
        List<Long> smIdOrder = new ArrayList<>(stopoversGroupedBySmId.keySet());

        // 각 smId에 대해 OptimizationRequest 생성
        List<OptimizationRequest> optimizationRequests = smIdOrder.stream()
                .map(smId -> new OptimizationRequest(
                        request.loadingStartTime(),
                        startStopover,
                        stopoversGroupedBySmId.get(smId)
                ))
                .collect(Collectors.toList());

        // 경로 최적화 API 호출 및 처리
        List<CourseResponse> courses = optimizationService.callOptimizationApi(request, optimizationRequests, smIdOrder, addressMapping);

        // 총 용적률 계산
        double totalVolume = 0;
        double totalWeight = 0;
        double maxLoadVolumeSum = 0;
        double maxLoadWeightSum = 0;

        for (CourseResponse course : courses) {
            for (CourseDetailResponse detail : course.getCourseDetailResponseList()) {
                totalVolume += detail.getVolume();
                totalWeight += detail.getWeight();
            }

            Vehicle vehicle = vehicleRepository.findBySm_Id(course.getCourseDetailResponseList().get(0).getSmId());
            maxLoadVolumeSum += vehicle.getMaxLoadVolume();
            maxLoadWeightSum += vehicle.getMaxLoadWeight();
        }

        int totalFloorAreaRatio = 0;
        String deliveryType = request.orderReuquestList().get(0).deliveryType();

        if ("지입".equals(deliveryType)) {
            totalFloorAreaRatio = (int)((totalWeight / maxLoadWeightSum) * 100);
        } else if ("택배".equals(deliveryType)) {
            totalFloorAreaRatio = (int)((totalVolume / maxLoadVolumeSum) * 100);
        }

        // 시작경유지 응답 생성
        StartStopoverResponse startStopoverResponse = StartStopoverResponse.builder()
                .centerId(centerId)
                .fullAddress(center.getCustomerAddress() + " " + center.getDetailAddress())
                .lat(center.getLatitude())
                .lon(center.getLongitude())
                .expectedServiceDuration(LocalTime.of(center.getDelayTime()/60,center.getDelayTime()%60,0))
                .build();

        // 최종 DispatchResponse를 반환
        return DispatchResponse.builder()
                .dispatchCode(generateDispatchCode(request, center))  // 배차코드 = 배차번호
                .dispatchName(request.dispatchName())  // 배차명
                .totalOrder(courses.stream()
                        .mapToInt(CourseResponse::getOrderNum).sum())  // 총 주문 수
                .totalErrorNum((int) courses.stream()
                        .filter(CourseResponse::isErrorYn).count())  // 오류주문 수
                .totalTime(courses.stream()
                        .mapToInt(CourseResponse::getTotalTime).sum())  // 총 예상시간
                .totalFloorAreaRatio(totalFloorAreaRatio)  // 총 용적률
                .loadingStartTime(request.loadingStartTime())  // 상차 시작 시간
                .startStopoverResponse(startStopoverResponse)  // 시작경유지 정보
                .course(courses)  // 경로별 리스트
                .build();
    }

    public void downloadOrderFormExcel(HttpServletResponse response) {
        List<TransportOrderExcelHeader> transportOrdersList = TransportOrderExcelHeader.getTransportOrders();

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFCellStyle nameRowStyle = createCellStyle(workbook, IndexedColors.GREY_40_PERCENT, FONT_SIZE_NAME, true,
                HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
        XSSFCellStyle requiredRowStyle = createCellStyle(workbook, IndexedColors.GREY_25_PERCENT, FONT_SIZE_REQUIRED,
                true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
        requiredRowStyle.getFont().setColor(IndexedColors.RED.getIndex());

        XSSFCellStyle commentRowStyle = createCellStyle(workbook, IndexedColors.LEMON_CHIFFON, (short) 0, false, null,
                VerticalAlignment.CENTER, true);
        commentRowStyle.setWrapText(true);

        XSSFCellStyle exampleRowStyle = createCellStyle(workbook, null, (short) 0, false, null,
                VerticalAlignment.CENTER, true);
        exampleRowStyle.setWrapText(true);

        XSSFSheet sheet = workbook.createSheet("운송_주문_양식");

        createRow(sheet, 0, transportOrdersList, nameRowStyle);
        createRow(sheet, 1, transportOrdersList, requiredRowStyle);
        createRow(sheet, 2, transportOrdersList, commentRowStyle);
        createRow(sheet, 3, transportOrdersList, exampleRowStyle);

        adjustColumnWidths(sheet, transportOrdersList.size());

        sheet.getRow(2).setHeight(COMMENT_ROW_HEIGHT);

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    public List<SmNameAndZipCodeResponse> validateSmNameAndZipCodes(List<SmNameAndZipCodeRequest> requests) {
        Map<String, Integer> smNameWithIdMap = smRepository.findAllSmNameWithIdsToMap();
        Map<String, Integer> zipWithIdMap = deliveryDestinationRepository.findAllZipCodeWithIdsToMap();

        return requests.stream()
                .map(request -> SmNameAndZipCodeResponse.builder()
                        .zipCodeValid(zipWithIdMap.containsKey(request.zipCode()))
                        .deliveryDestinationId(zipWithIdMap.getOrDefault(request.zipCode(), 0))
                        .smNameValid(smNameWithIdMap.containsKey(request.smName()))
                        .smId(smNameWithIdMap.getOrDefault(request.smName(), 0))
                        .build())
                .toList();
    }

    private String generateDispatchCode(TransportOrderRequest request, Center center) {
        // 상차 시작 일시를 포맷팅
        String formattedDate = request.loadingStartTime().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 센터 코드 가져오기
        String centerCode = center.getCenterCode();

        // 센터에 해당하는 상차 시작 일시(시간 포함) 이전의 배차 개수 조회
        int dispatchCount = dispatchNumberRepository.countByCenterAndLoadingStartTimeBefore(center, request.loadingStartTime());

        // 배차 번호 생성: 개수 + 1
        String dispatchNumber = String.valueOf(dispatchCount + 1);

        // 최종 디스패치 코드 생성
        return formattedDate + centerCode + "#" + dispatchNumber;
    }

    private XSSFCellStyle createCellStyle(XSSFWorkbook workbook, IndexedColors backgroundColor, short fontSize,
                                          boolean bold, HorizontalAlignment hAlign, VerticalAlignment vAlign,
                                          boolean border) {
        XSSFCellStyle style = workbook.createCellStyle();
        if (backgroundColor != null) {
            style.setFillForegroundColor(backgroundColor.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (fontSize > 0 || bold) {
            XSSFFont font = workbook.createFont();
            font.setFontHeightInPoints(fontSize);
            font.setBold(bold);
            style.setFont(font);
        }
        if (hAlign != null) {
            style.setAlignment(hAlign);
        }
        if (vAlign != null) {
            style.setVerticalAlignment(vAlign);
        }
        if (border) {
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
        }
        return style;
    }

    private void createRow(XSSFSheet sheet, int rowIndex, List<TransportOrderExcelHeader> transportOrdersList,
                           XSSFCellStyle style) {
        Row row = sheet.createRow(rowIndex);
        int cellNumber = 0;
        for (TransportOrderExcelHeader header : transportOrdersList) {
            Cell cell = row.createCell(cellNumber++);
            if (rowIndex == 0) {
                cell.setCellValue(header.name());
            } else if (rowIndex == 1) {
                cell.setCellValue(header.required());
            } else if (rowIndex == 2) {
                cell.setCellValue(header.comment());
            } else if (rowIndex == 3) {
                cell.setCellValue(header.example());
            }
            cell.setCellStyle(style);
        }
    }

    private void adjustColumnWidths(XSSFSheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + COLUMN_PADDING);
        }
    }

    @Transactional(readOnly = true)
    public TransportOrderResponse getTransportOrderById(Long transportOrderId,Long destinationId) {
        TransportOrder transportOrder = transportOrderRepository.findOrderWithDispatchDetailByIdOrThrow(transportOrderId);

        if (destinationId != null){
            DeliveryDestination deliveryDestination = deliveryDestinationRepository.findByIdOrThrow(destinationId);
            return TransportOrderResponse.of(transportOrder,deliveryDestination.getAdminName(),deliveryDestination.getPhoneNumber(),destinationId);
        }
        return TransportOrderResponse.of(transportOrder);
    }
}

