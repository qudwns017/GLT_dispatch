package com.team2.finalproject.domain.dispatch.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatch.exception.DispatchErrorCode;
import com.team2.finalproject.domain.dispatch.exception.DispatchException;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchSearchRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.IssueRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchSearchResponse;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.dispatchdetail.repository.DispatchDetailRepository;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.transportorder.repository.TransportOrderRepository;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DispatchService {
    private final DispatchRepository dispatchRepository;
    private final DispatchNumberRepository dispatchNumberRepository;
    private final UsersRepository usersRepository;
    private final SmRepository smRepository;
    private final TransportOrderRepository transportOrderRepository;
    private final DispatchDetailRepository dispatchDetailRepository;

    @Transactional(readOnly = true)
    public DispatchSearchResponse searchDispatches(DispatchSearchRequest request, Long userId) {
        Users users = usersRepository.findByIdOrThrow(userId);
        Center center = users.getCenter();
        LocalDateTime startDateTime = request.startDate().atStartOfDay();
        LocalDateTime endDateTime = request.endDateTime();

        // 검색 기간, 검색 옵션, 담당자 체크 여부에 따른 필터
        List<DispatchNumber> dispatchNumbers =
                searchDispatchNumbers(request, users, center, startDateTime, endDateTime);
        log.info("검색 후 배차(dispatchNumbers) 개수: {}", dispatchNumbers.size());

        // 각 상태에 해당하는 개수 구하기
        // Dispatch 내부의 상태와 달라 DispatchNumber 에서 구해야 함
        long inTransit = dispatchNumbers.stream()
                .filter(d -> d.getStatus().name().equals("IN_TRANSIT")).count();
        long waiting = dispatchNumbers.stream()
                .filter(d -> d.getStatus().name().equals("WAITING")).count();
        long completed = dispatchNumbers.stream()
                .filter(d -> d.getStatus().name().equals("COMPLETED")).count();

        // 검색을 원하는 status
        DispatchNumberStatus status = request.status();

        // 원하는 status에 해당하는 DispatchNumber만 필터링
        List<DispatchNumber> filteredDispatchNumbers = dispatchNumbers.stream()
                .filter(d -> d.getStatus() == status)
                .toList();

        log.info("status 필터 후 배차(dispatchNumbers) 개수: {}", filteredDispatchNumbers.size());

        //  Map<DispatchNumber, List<Dispatch>>
        Map<DispatchNumber, List<Dispatch>> dispatchMap = dispatchRepository.findDispatchMapByDispatchNumbers(filteredDispatchNumbers);

        log.info("dispatchMap: {}", dispatchMap.size());

        List<DispatchSearchResponse.DispatchResult> results = new ArrayList<>();
        // 기사 수, 전체 주문 수, 완료 주문 수 구하기
        for (Map.Entry<DispatchNumber, List<Dispatch>> entry : dispatchMap.entrySet()) {
            DispatchNumber dispatchNumber = entry.getKey();
            List<Dispatch> dispatches = entry.getValue();

            int smNum = dispatches.size();
            int totalOrder = dispatches.stream().mapToInt(Dispatch::getDeliveryOrderCount).sum();
            int completedOrder = dispatches.stream().mapToInt(Dispatch::getCompletedOrderCount).sum();

            // 대기 중이면 0%, 배송 완료면 100%, 그 외에는 진행율 계산
            int progress = 0;
            if (status.name().equals("COMPLETED")) {
                progress = 100;
            } else if (status.name().equals("IN_TRANSIT")) {
                progress = calcProgress(totalOrder, completedOrder);
            }

            results.add(DispatchSearchResponse.DispatchResult.builder()
                    .dispatchNumberId(dispatchNumber.getId())
                    .progress(progress)
                    .dispatchCode(dispatchNumber.getDispatchNumber())
                    .dispatchName(dispatchNumber.getDispatchName())
                    .startDateTime(dispatchNumber.getLoadingStartTime())
                    .totalOrder(totalOrder)
                    .smNum(smNum)
                    .manager(dispatchNumber.getUsers().getName())
                    .build());
        }

        return DispatchSearchResponse.builder()
                .inProgress((int)inTransit)
                .waiting((int)waiting)
                .completed((int)completed)
                .results(results)
                .build();

    }

    // 배차 탭에서의 배차 취소
    @Transactional
    public void cancelDispatch(DispatchCancelRequest request) {

        if(request.isInTransit()){
            // 주행 중인 경우
            cancelInTransitDispatch(request.dispatchNumberIds());
        }else{
            // 주행 대기인 경우 -  해당하는 DispatchNumber, Dispatch, DispatchDetail, Transport_order 모두 삭제
            dispatchNumberRepository.deleteByIdIn(request.dispatchNumberIds());
        }
    }

    // 검색 기간, 검색 옵션, 담당자 체크 여부에 따른 필터
    private List<DispatchNumber> searchDispatchNumbers(DispatchSearchRequest request, Users users, Center center,
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
    private List<DispatchNumber> searchByDispatchCode(DispatchSearchRequest request, Users users, Center center,
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
    private List<DispatchNumber> searchByDispatchName(DispatchSearchRequest request, Users users, Center center,
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
    private List<DispatchNumber> searchByManager(DispatchSearchRequest request, Users users, Center center,
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
    private List<DispatchNumber> searchByDriverId(DispatchSearchRequest request, Users users, Center center,
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

    // 진행률 계산
    private int calcProgress(int totalOrder, int completedOrder) {
        if (totalOrder == 0) {
            return 0;
        }
        return (int) Math.round((double) completedOrder / totalOrder * 100);
    }

    // 주행 중인 경우 배차 취소
    private void cancelInTransitDispatch(List<Long> dispatchNumberIds) {
        //  dispatchNumberIds에 해당하는 배차 코드를 지닌 DispatchNumber 리스트 조회
        List<DispatchNumber> dispatchNumbersToCancel = dispatchNumberRepository.findByIdIn(dispatchNumberIds);

        // 기사 주행 중 -> 주행 대기
        updateSmStatusToNotDriving(dispatchNumbersToCancel);

        // DispatchNumber 상태 COMPLETED로 변경
        updateDispatchNumberStatusToCompleted(dispatchNumbersToCancel);

        // 각 Dispatch 처리 (내부의 DispatchDetail, TransportOrder 포함)
        dispatchNumbersToCancel.stream()
                .flatMap(dn -> dn.getDispatchList().stream())
                .forEach(this::processDispatchCancellation);
    }

    // 기사 배송 상태 변경
    private void updateSmStatusToNotDriving(List<DispatchNumber> dispatchNumbers) {
        dispatchNumbers.stream()
                .flatMap(dn -> dn.getDispatchList().stream())
                .map(Dispatch::getSm)
                .distinct()
                .forEach(sm -> {
                    sm.setIsDriving(false);
                    smRepository.save(sm);
                });
    }

    // DispatchNumber 상태 COMPLETED로 변경
    private void updateDispatchNumberStatusToCompleted(List<DispatchNumber> dispatchNumbers) {
        dispatchNumbers.forEach(dn -> {
            dn.setStatus(DispatchNumberStatus.COMPLETED);
            dispatchNumberRepository.save(dn);
        });
    }

    // Dispatch 취소 처리
    // Dispatch 상태 COMPLETED로 변경, 미 배송 상태 취소로 변경, 총 주문 수에서 취소 주문 수 빼기, 운송 주문 보류 처리
    private void processDispatchCancellation(Dispatch dispatch) {
        // Dispatch 상태 COMPLETED로 변경
        dispatch.setDeliveryStatus(DispatchStatus.COMPLETED);

        // 미 배송된 DispatchDetail 개수
        long undeliveredCount = dispatch.getDispatchDetailList().stream()
                .filter(dd -> dd.getDispatchDetailStatus() != DispatchDetailStatus.WORK_COMPLETED)
                .count();

        // 미 배송된 DispatchDetail의 상태를 CANCELED로 변경
        dispatch.getDispatchDetailList().stream()
                .filter(dd -> dd.getDispatchDetailStatus() != DispatchDetailStatus.WORK_COMPLETED)
                .forEach(dd -> {
                    dd.setDispatchDetailStatus(DispatchDetailStatus.CANCELED);
                    dispatchDetailRepository.save(dd); // DispatchDetail 업데이트

                    // TransportOrder isPending = true로 변경
                    TransportOrder transportOrder = dd.getTransportOrder();
                    if (transportOrder != null) {
                        transportOrder.setPending(true);
                        transportOrderRepository.save(transportOrder);
                    }
                });

        // Dispatch 총 주문수에서 미 배송된 수만큼 빼기
        dispatch.setDeliveryOrderCount(dispatch.getDeliveryOrderCount() - (int) undeliveredCount);

        // Dispatch 엔티티 업데이트
        dispatchRepository.save(dispatch);
    }

    public void updateIssue(long dispatchId, IssueRequest request) {
        Dispatch dispatch = dispatchRepository.findByIdOrThrow(dispatchId);
        dispatch.update(request);
        dispatchRepository.save(dispatch);
    }
}
