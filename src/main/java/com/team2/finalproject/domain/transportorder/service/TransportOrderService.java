package com.team2.finalproject.domain.transportorder.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.dto.response.CourseResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.StartStopoverResponse;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.dto.request.OrderRequest;
import com.team2.finalproject.domain.transportorder.model.dto.request.SmNameRequest;
import com.team2.finalproject.domain.transportorder.model.dto.request.TransportOrderRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndSmIdResponse;
import com.team2.finalproject.domain.transportorder.model.dto.response.TransportOrderResponse;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.transportorder.repository.TransportOrderRepository;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicle.repository.VehicleRepository;
import com.team2.finalproject.global.service.KakaoApiService;
import com.team2.finalproject.global.service.OptimizationService;
import com.team2.finalproject.global.util.TransportOrderUtil;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private final VehicleRepository vehicleRepository;
    private final KakaoApiService kakaoApiService;
    private final OptimizationService optimizationService;
    private final TransportOrderRepository transportOrderRepository;


    private static final short FONT_SIZE_NAME = 14;
    private static final short FONT_SIZE_REQUIRED = 12;
    private static final short COMMENT_ROW_HEIGHT = 2000;
    private static final int COLUMN_PADDING = 8192;

    @Transactional
    public DispatchResponse processTransportOrder(TransportOrderRequest request, Users user) {
        // Center 조회 및 시작 경유지 생성
        Center center = getCenterByUser(user);
        Stopover startStopover = createStartStopover(center);

        // 지번 주소와 도로명 주소 간의 매핑을 위한 맵 생성(도로명 주소로 로직을 모두 계산하고 마지막에 해당 지번 주소 출력을 위한 맵)
        Map<String, String[]> addressMapping = new HashMap<>();

        // 주문을 smId별로 분류하고, 각 주소를 키값으로 주문 매핑(최적화 후 응답받은 경유지들과 매칭 예정)
        Map<Long, Map<String, List<OrderRequest>>> mapOrderAndAddressBySmId = MapOrderAndAddressBySmId(request);

        // 주문의 주소에 해당하는 stopover 제작 및 stopover를 smId별로 분류하여 매핑(기사별로 경로를 최적화하기 위함)
        Map<Long, List<Stopover>> stopoversGroupedBySmId = groupStopoversBySmId(request, addressMapping);

        // 최적화 요청 생성 및 경로 최적화
        List<OptimizationRequest> optimizationRequests =
                createOptimizationRequests(request, startStopover, stopoversGroupedBySmId);
        List<CourseResponse> courses = optimizationService.
                callOptimizationApi(optimizationRequests, mapOrderAndAddressBySmId, addressMapping);

        // 용적률 계산
        int totalFloorAreaRatio = calculateTotalFloorAreaRatio(request, courses);

        // 출발지 응답 생성
        StartStopoverResponse startStopoverResponse = createStartStopoverResponse(request, center);

        // 최종 DispatchResponse 반환
        return createDispatchResponse(request, center, courses, totalFloorAreaRatio, startStopoverResponse);
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

    public List<SmNameAndSmIdResponse> validateSmNames(List<SmNameRequest> requests) {
        return requests.stream()
                .map(request -> {
                    Long smId = smRepository.findSmIdBySmName(request.smName());
                    boolean isValid = smId != null;
                    int resultSmId = isValid ? smId.intValue() : 0;

                    return SmNameAndSmIdResponse.builder()
                            .smNameValid(isValid)
                            .smId(resultSmId)
                            .build();
                })
                .toList();
    }

    private Center getCenterByUser(Users user) {
        Long centerId = user.getCenter().getId();
        return centerRepository.findById(centerId).orElseThrow();
    }

    private Stopover createStartStopover(Center center) {
        return Stopover.builder()
                .address(center.getRoadAddress() + " " + center.getDetailAddress())
                .lat(center.getLatitude())
                .lon(center.getLongitude())
                .delayTime(LocalTime.of(center.getDelayTime() / 60, center.getDelayTime() % 60, 0))
                .build();
    }

    private Map<Long, Map<String, List<OrderRequest>>> MapOrderAndAddressBySmId(TransportOrderRequest request) {
        return request.orderReuquestList().stream()
                .collect(Collectors.groupingBy(
                        OrderRequest::smId,
                        LinkedHashMap::new, // smId 순서를 유지하기 위해 LinkedHashMap 사용
                        Collectors.groupingBy(
                                order -> order.address() + " " + order.detailAddress(),
                                LinkedHashMap::new, // 주소 순서를 유지하기 위해 LinkedHashMap 사용
                                Collectors.toList()
                        )
                ));
    }

    private Map<Long, List<Stopover>> groupStopoversBySmId(TransportOrderRequest request,
                                                           Map<String, String[]> addressMapping) {
        return request.orderReuquestList().stream()
                .collect(Collectors.groupingBy(
                        OrderRequest::smId,
                        LinkedHashMap::new,  // 순서 유지를 위해 LinkedHashMap 사용
                        Collectors.mapping(order -> createStopover(order, addressMapping), Collectors.toList())
                ));
    }

    private Stopover createStopover(OrderRequest order, Map<String, String[]> addressMapping) {
        DeliveryDestination destination = deliveryDestinationRepository
                .findByFullAddress(order.address(), order.detailAddress());
        if (destination != null) {
            return mapExistingDestinationToStopover(destination, order.expectedServiceDuration(), addressMapping);
        }
        return mapNewAddressToStopover(order, addressMapping);
    }

    private Stopover mapExistingDestinationToStopover(DeliveryDestination destination, int expectedServiceDuration,
                                                      Map<String, String[]> addressMapping) {
        String roadAddress = destination.getRoadAddress() + " " + destination.getDetailAddress();
        String[] addressArray = {destination.getLotNumberAddress(), destination.getDetailAddress()};
        addressMapping.put(roadAddress, addressArray);

        return new Stopover(
                roadAddress,
                destination.getLatitude(),
                destination.getLongitude(),
                LocalTime.of((destination.getDelayTime() + expectedServiceDuration) / 60,
                        (destination.getDelayTime() + expectedServiceDuration) % 60, 0)
        );
    }

    private Stopover mapNewAddressToStopover(OrderRequest order, Map<String, String[]> addressMapping) {
        AddressInfo addressInfo = kakaoApiService
                .getAddressInfoFromKakaoAPI(order.address() + " " + order.detailAddress());

        String roadAddress = order.address() + " " + order.detailAddress();
        String[] addressArray = {addressInfo.getCustomerAddress(), order.detailAddress()};
        addressMapping.put(roadAddress, addressArray);

        return new Stopover(
                roadAddress,
                addressInfo.getLat(),
                addressInfo.getLon(),
                LocalTime.of(order.expectedServiceDuration() / 60,
                        order.expectedServiceDuration() % 60, 0)
        );
    }

    private List<OptimizationRequest> createOptimizationRequests(TransportOrderRequest request,
                                                                 Stopover startStopover,
                                                                 Map<Long, List<Stopover>> stopoversGroupedBySmId) {
        return stopoversGroupedBySmId.values().stream()
                .map(stopovers -> new OptimizationRequest(
                        request.loadingStartTime(),
                        startStopover,
                        stopovers
                ))
                .collect(Collectors.toList());
    }


    private int calculateTotalFloorAreaRatio(TransportOrderRequest request, List<CourseResponse> courses) {
        double totalVolume = 0;
        double totalWeight = 0;
        double maxLoadVolumeSum = 0;
        double maxLoadWeightSum = 0;

        for (CourseResponse course : courses) {
            for (CourseResponse.CourseDetailResponse detail : course.getCourseDetailResponseList()) {
                totalVolume += detail.getVolume();
                totalWeight += detail.getWeight();
            }

            Vehicle vehicle = vehicleRepository.findBySm_Id(course.getCourseDetailResponseList().get(0).getSmId());
            maxLoadVolumeSum += vehicle.getMaxLoadVolume();
            maxLoadWeightSum += vehicle.getMaxLoadWeight();
        }

        String deliveryType = request.orderReuquestList().get(0).deliveryType();
        if ("지입".equals(deliveryType)) {
            return (int) ((totalWeight / maxLoadWeightSum) * 100);
        } else if ("택배".equals(deliveryType)) {
            return (int) ((totalVolume / maxLoadVolumeSum) * 100);
        }

        return 0;
    }

    private StartStopoverResponse createStartStopoverResponse(TransportOrderRequest request, Center center) {
        return StartStopoverResponse.builder()
                .centerId(center.getId())
                .fullAddress(center.getLotNumberAddress() + " " + center.getDetailAddress())
                .lat(center.getLatitude())
                .lon(center.getLongitude())
                .expectedServiceDuration(LocalTime.of(center.getDelayTime() / 60,
                        center.getDelayTime() % 60, 0))
                .departureTime(TransportOrderUtil.addDelayTime(request.loadingStartTime(),
                        LocalTime.of(center.getDelayTime() / 60, center.getDelayTime() % 60, 0)))
                .build();
    }

    private DispatchResponse createDispatchResponse(TransportOrderRequest request, Center center,
                                                    List<CourseResponse> courses, int totalFloorAreaRatio,
                                                    StartStopoverResponse startStopoverResponse) {
        return DispatchResponse.builder()
                .dispatchCode(generateDispatchCode(request, center))
                .dispatchName(request.dispatchName())
                .totalOrder(courses.stream().mapToInt(CourseResponse::getOrderNum).sum())
                .totalErrorNum((int) courses.stream().filter(CourseResponse::isErrorYn).count())
                .totalTime(courses.stream().mapToInt(CourseResponse::getTotalTime).sum())
                .totalFloorAreaRatio(totalFloorAreaRatio)
                .loadingStartTime(request.loadingStartTime())
                .startStopoverResponse(startStopoverResponse)
                .course(courses)
                .build();
    }

    private String generateDispatchCode(TransportOrderRequest request, Center center) {
        // 상차 시작 일시를 포맷팅
        String formattedDate = request.loadingStartTime().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 센터 코드 가져오기
        String centerCode = center.getCenterCode();

        // 센터에 해당하는 당일 날짜의 배차 개수 조회
        int dispatchCount = dispatchNumberRepository.countByCenterAndLoadingStartTimeOnDate(center, request.loadingStartTime());

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
    public TransportOrderResponse getTransportOrderById(Long transportOrderId) {
        TransportOrder transportOrder = transportOrderRepository.findOrderWithDispatchDetailByIdOrThrow(transportOrderId);
        Long destinationId = transportOrder.getDispatchDetail().getDestinationId();

        if (destinationId != null){
            DeliveryDestination deliveryDestination = deliveryDestinationRepository.findByIdOrThrow(destinationId);
            return TransportOrderResponse.of(transportOrder,deliveryDestination.getManagerName(),deliveryDestination.getPhoneNumber(),destinationId);
        }

        return TransportOrderResponse.of(transportOrder,transportOrder.getCustomerName(),transportOrder.getCustomerPhoneNumber(),transportOrder.getRoadAddress(),transportOrder.getDetailAddress(),transportOrder.getCustomerNotes());
    }
}

