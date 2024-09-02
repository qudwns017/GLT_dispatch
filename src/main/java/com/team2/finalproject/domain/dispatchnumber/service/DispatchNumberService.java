package com.team2.finalproject.domain.dispatchnumber.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.exception.DispatchErrorCode;
import com.team2.finalproject.domain.dispatch.exception.DispatchException;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.dispatchnumber.model.dto.request.DispatchNumberSearchRequest;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse.DispatchSimpleResponse;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchNumberSearchResponse;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.repository.UsersRepository;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DispatchNumberService {

    private final DispatchNumberRepository dispatchNumberRepository;
    private final DeliveryDestinationRepository deliveryDestinationRepository;
    private final UsersRepository usersRepository;
    private final DispatchRepository dispatchRepository;

    public DispatchListResponse getDispatchList(Long dispatchCodeId){

        DispatchNumber dispatchNumber = dispatchNumberRepository.findByIdWithJoinOrThrow(dispatchCodeId);

        List<Dispatch> dispatchList = dispatchNumber.getDispatchList();

        int totalCompletedOrderCount = dispatchList.stream()
            .mapToInt(Dispatch::getCompletedOrderCount) // Dispatch 객체의 getCompletedOrderCount 값을 int로 변환
            .sum(); // 모든 값을 합산
        int totalDeliveryOrderCount = dispatchList.stream()
            .mapToInt(Dispatch::getDeliveryOrderCount)
            .sum();
        double totalProgressionRate = (double) totalDeliveryOrderCount / totalCompletedOrderCount * 100;

        Center center = dispatchNumber.getCenter();
        DispatchListResponse.StartStopover startStopover = DispatchListResponse.StartStopover.of(center.getId(),center.getLotNumberAddress(),center.getLatitude(),center.getLongitude(), center.getDelayTime());

        List<DispatchListResponse.Issue> issueList = new ArrayList<>();

        List<DispatchSimpleResponse> dispatchResponseList = dispatchList.stream()
            .map((dispatch) -> {
                double progressionRate = (double) dispatch.getDeliveryOrderCount() / dispatch.getCompletedOrderCount() * 100;

                List<Map<String, Double>> stopoverList = createStopoverList(dispatch);
                List<Map<String, Double>> coordinates = createCoordinateList(dispatch);

                issueList.addAll(getIssueListOfDispatch(dispatch, dispatchCodeId));

                return DispatchSimpleResponse.of(dispatch.getId(), dispatch.getDeliveryStatus().getDescription(), dispatch.getSmName(), dispatch.getCompletedOrderCount(), dispatch.getDeliveryOrderCount(), (int) progressionRate,stopoverList,coordinates );
            }).toList();

        return DispatchListResponse.of(dispatchNumber.getDispatchNumber(), dispatchNumber.getDispatchName(),(int) totalProgressionRate,totalCompletedOrderCount,totalDeliveryOrderCount,issueList.size(),startStopover,dispatchResponseList,issueList);
    }

    @Transactional(readOnly = true)
    public DispatchNumberSearchResponse searchDispatches(DispatchNumberSearchRequest request, UserDetailsImpl userDetails) {
        Users users = userDetails.getUsers();
        Center center = userDetails.getCenter();
        LocalDateTime startDateTime = request.startDate().atStartOfDay();
        LocalDateTime endDateTime = request.endDateTime();

        // 검색 기간, 검색 옵션, 담당자 체크 여부에 따른 필터
        List<DispatchNumber> dispatchNumbers =
                searchDispatchNumbers(request, users, center, startDateTime, endDateTime);
        log.info("검색 후 배차(dispatchNumbers) 개수: {}", dispatchNumbers.size());

        // 각 상태에 해당하는 개수 구하기
        // Dispatch 내부의 상태와 달라 DispatchNumber 에서 구해야 함
        long inTransitCount = countDispatchNumbersByStatus(dispatchNumbers, "IN_TRANSIT");
        long waitingCount = countDispatchNumbersByStatus(dispatchNumbers, "WAITING");
        long completedCount = countDispatchNumbersByStatus(dispatchNumbers, "COMPLETED");

        // 검색을 원하는 status
        DispatchNumberStatus status = request.status();

        // 원하는 status에 해당하는 DispatchNumber만 필터링
        List<DispatchNumber> filteredDispatchNumbers = filterDispatchNumbersByStatus(dispatchNumbers, status);
        log.info("status 필터 후 배차(dispatchNumbers) 개수: {}", filteredDispatchNumbers.size());

        //  Map<DispatchNumber, List<Dispatch>>
        Map<DispatchNumber, List<Dispatch>> dispatchMap = dispatchRepository.findDispatchMapByDispatchNumbers(filteredDispatchNumbers);
        log.info("dispatchMap: {}", dispatchMap.size());

        // DispatchNumberSearchResponse.DispatchResult 생성
        List<DispatchNumberSearchResponse.DispatchResult> results = createDispatchResults(dispatchMap, status);

        return DispatchNumberSearchResponse.builder()
                .inProgress((int)inTransitCount)
                .waiting((int)waitingCount)
                .completed((int)completedCount)
                .results(results)
                .build();

    }

    private List<Map<String, Double>> createStopoverList(Dispatch dispatch) {
        return dispatch.getDispatchDetailList().stream()
            .map(detail -> {
                Map<String, Double> stopover = new HashMap<>();
                stopover.put("lat", detail.getDestinationLatitude());
                stopover.put("lon", detail.getDestinationLongitude());
                return stopover;
            }).toList();
    }

    private List<Map<String, Double>> createCoordinateList(Dispatch dispatch) {
        return Arrays.stream(dispatch.getPath().getCoordinates())
            .map(coord -> {
                Map<String, Double> coordinate = new HashMap<>();
                coordinate.put("lat", coord.getX());
                coordinate.put("lon", coord.getY());
                return coordinate;
            }).toList();
    }

    private List<DispatchListResponse.Issue> getIssueListOfDispatch(Dispatch dispatch, Long dispatchCodeId) {
        List<DispatchListResponse.Issue> issueList = new ArrayList<>();

        for (DispatchDetail dispatchDetail : dispatch.getDispatchDetailList()) {
            if (dispatchDetail.getDispatchDetailStatus() == DispatchDetailStatus.DELIVERY_DELAY) {
                String comment = null;
                if (dispatchDetail.getDestinationId() != null) {
                    comment = deliveryDestinationRepository.findByIdOrThrow(dispatchDetail.getDestinationId()).getComment();
                }
                DispatchListResponse.Issue issue = DispatchListResponse.Issue.of(
                    dispatchCodeId,
                    dispatch.getId(),
                    dispatch.getSmName(),
                    dispatchDetail.getTransportOrder().getLotNumberAddress(),
                    dispatchDetail.getDestinationId(),
                    comment
                );
                issueList.add(issue);
            }
        }
        return issueList;
    }

    // 검색 기간, 검색 옵션, 담당자 체크 여부에 따른 필터
    private List<DispatchNumber> searchDispatchNumbers(DispatchNumberSearchRequest request, Users users, Center center,
                                                       LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("검색 옵션: {}", request.searchOption());
        boolean isManager = request.isManager(); // 담당자 여부
        // 검색 옵션에 다른 검색 (검색창 검색)
        if(request.searchOption() != null) {
            // 배차 코드 검색
            return switch (request.searchOption()) {
                case "dispatchCode" -> searchByDispatchCode(request, users, center,
                        startDateTime, endDateTime, isManager);
                // 배차 명 검색
                case "dispatchName" -> searchByDispatchName(request, users, center,
                        startDateTime, endDateTime, isManager);
                // 배차 담당자 검색
                case "manager" -> searchByManager(request, users, center,
                        startDateTime, endDateTime, isManager);
                // 기사 검색
                case "driver" -> searchByDriverId(request, users, center,
                        startDateTime, endDateTime, isManager);
                // 검색이 없는 경우
                case "" -> searchByDefault(users, center, startDateTime, endDateTime, isManager);
                default -> throw new DispatchException(DispatchErrorCode.WRONG_SEARCH_OPTION);
            };
        }
        return searchByDefault(users, center, startDateTime, endDateTime, isManager);
    }

    // 배차 코드에 대한 검색
    private List<DispatchNumber> searchByDispatchCode(DispatchNumberSearchRequest request, Users users, Center center,
                                                      LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        log.info("배송 코드 검색: {}", request.searchKeyword());
        if (isManager) {
            return dispatchNumberRepository.findByCenterAndUsersAndDispatchCodeAndLoadStartDateTimeBetween(
                    center, users, request.searchKeyword(), startDateTime, endDateTime);
        }
        return dispatchNumberRepository.findByCenterAndDispatchCodeAndLoadStartDateTimeBetween(
                center, request.searchKeyword(), startDateTime, endDateTime);
    }

    // 배차 명에 대한 검색
    private List<DispatchNumber> searchByDispatchName(DispatchNumberSearchRequest request, Users users, Center center,
                                                      LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        log.info("배송 명 검색: {}", request.searchKeyword());
        if (isManager) {
            return dispatchNumberRepository.findByCenterAndUsersAndDispatchNameAndLoadStartDateTimeBetween(
                    center, users, request.searchKeyword(), startDateTime, endDateTime);
        }
        return dispatchNumberRepository.findByCenterAndDispatchNameAndLoadStartDateTimeBetween(
                center, request.searchKeyword(), startDateTime, endDateTime);
    }

    // 담당자에 대한 검색
    private List<DispatchNumber> searchByManager(DispatchNumberSearchRequest request, Users users, Center center,
                                                 LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        Users manager = usersRepository.findByNameOrNull(request.searchKeyword());
        log.info("담당자 검색: {}", request.searchKeyword());
        // 자신 담당만 보기 & 다른 사람 검색 이면 빈 값 출력
        if (isManager && !users.equals(manager)) {
            return new ArrayList<>();
        }
        return dispatchNumberRepository.findByCenterAndUsersAndLoadStartDateTimeBetween(
                center, manager, startDateTime, endDateTime);
    }

    // 기사에 대한 검색
    private List<DispatchNumber> searchByDriverId(DispatchNumberSearchRequest request, Users users, Center center,
                                                  LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        log.info("기사 검색: {}", request.searchKeyword());
        if (isManager) {
            return dispatchNumberRepository.findByCenterAndUsersAndSmNameAndLoadStartDateTimeBetween(
                    center, users, request.searchKeyword(), startDateTime, endDateTime);
        }
        return dispatchNumberRepository.findByCenterAndSmNameAndLoadStartDateTimeBetween(
                center, request.searchKeyword(), startDateTime, endDateTime);
    }

    // 검색이 없는 경우
    private List<DispatchNumber> searchByDefault(Users users, Center center, LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime, boolean isManager) {
        log.info("검색 옵션 없음");
        if (isManager) {
            return dispatchNumberRepository.findByCenterAndUsersAndLoadStartDateTimeBetween(
                    center, users, startDateTime, endDateTime);
        }
        return dispatchNumberRepository.findByCenterAndLoadStartDateTimeBetween(
                center, startDateTime, endDateTime);
    }

    // 상태에 따른 DispatchNumber 개수
    private long countDispatchNumbersByStatus(List<DispatchNumber> dispatchNumbers, String statusName) {
        return dispatchNumbers.stream()
                .filter(d -> d.getStatus().name().equals(statusName))
                .count();
    }

    // 상태에 따른 DispatchNumber 필터
    private List<DispatchNumber> filterDispatchNumbersByStatus(List<DispatchNumber> dispatchNumbers, DispatchNumberStatus status) {
        return dispatchNumbers.stream()
                .filter(d -> d.getStatus() == status)
                .toList();
    }

    // DispatchResult 리스트 생성
    private List<DispatchNumberSearchResponse.DispatchResult> createDispatchResults(Map<DispatchNumber, List<Dispatch>> dispatchMap, DispatchNumberStatus status) {
        List<DispatchNumberSearchResponse.DispatchResult> results = new ArrayList<>();
        for (Map.Entry<DispatchNumber, List<Dispatch>> entry : dispatchMap.entrySet()) {
            DispatchNumber dispatchNumber = entry.getKey();
            List<Dispatch> dispatches = entry.getValue();

            int smNum = dispatches.size();
            int totalOrder = dispatches.stream().mapToInt(Dispatch::getDeliveryOrderCount).sum();
            int completedOrder = dispatches.stream().mapToInt(Dispatch::getCompletedOrderCount).sum();

            int progress = calculateProgress(status, totalOrder, completedOrder);

            results.add(DispatchNumberSearchResponse.DispatchResult.builder()
                    .dispatchNumberId(dispatchNumber.getId())
                    .progress(progress)
                    .dispatchCode(dispatchNumber.getDispatchNumber())
                    .dispatchName(dispatchNumber.getDispatchName())
                    .startDateTime(dispatchNumber.getLoadingStartTime())
                    .totalOrder(totalOrder)
                    .smNum(smNum)
                    .manager(dispatchNumber.getManager().getName())
                    .build());
        }
        return results;
    }

    // 진행률 계산
    private int calculateProgress(DispatchNumberStatus status, int totalOrder, int completedOrder) {
        if (status.name().equals("COMPLETED")) {
            return 100;
        } else if (status.name().equals("IN_TRANSIT")) {
            if (totalOrder == 0) {
                return 0;
            }
            return (int) Math.round((double) completedOrder / totalOrder * 100);
        }
        return 0;
    }
}
