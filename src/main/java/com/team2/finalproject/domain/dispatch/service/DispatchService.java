package com.team2.finalproject.domain.dispatch.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatch.exception.DispatchErrorCode;
import com.team2.finalproject.domain.dispatch.exception.DispatchException;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchSearchRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchSearchResponse;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
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

    @Transactional(readOnly = true)
    public DispatchSearchResponse searchDispatches(DispatchSearchRequest request, Long userId) {
        Users users = usersRepository.findByIdOrThrow(userId);
        Center center = users.getCenter();
        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDateTime();

        // 검색 기간, 검색 옵션, 담당자 체크 여부에 따른 필터
        List<DispatchNumber> dispatchNumbers =
                searchDispatchNumbers(request, users, center, startDateTime, endDateTime);

        // 각 상태에 해당하는 개수 구하기
        // Dispatch 내부의 상태와 달라 DispatchNumber 에서 구해야 함
        long inTransit = dispatchNumbers.stream()
                .filter(d -> d.getStatus().name().equals("IN_TRANSIT")).count();
        long waiting = dispatchNumbers.stream()
                .filter(d -> d.getStatus().name().equals("WAITING")).count();
        long completed = dispatchNumbers.stream()
                .filter(d -> d.getStatus().name().equals("COMPLETED")).count();

        // 검색을 원하는 status
        DispatchNumberStatus status = request.getStatus();

        // 원하는 status에 해당한는 Map<DispatchNumber, List<Dispatch>>
        Map<DispatchNumber, List<Dispatch>> dispatchMap = dispatchRepository.findDispatchMapByDispatchNumbersAndStatus(dispatchNumbers, status);

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

    // 검색 기간, 검색 옵션, 담당자 체크 여부에 따른 필터
    private List<DispatchNumber> searchDispatchNumbers(DispatchSearchRequest request, Users users, Center center,
                                                       LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("검색 옵션: {}", request.getSearchOption());
        List<DispatchNumber> dispatchNumbers;
        boolean isManager = request.getIsManager(); // 담당자 여부
        // 검색 옵션에 다른 검색 (검색창 검색)
        if(request.getSearchOption() != null) {
            // 배차 코드 검색
            dispatchNumbers = switch (request.getSearchOption()) {
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
                default -> throw new DispatchException(DispatchErrorCode.WRONG_SEARCH_OPTION);
            };
        }else{
            dispatchNumbers = searchByDefault(users, center,
                    startDateTime, endDateTime, isManager);
        }
        return dispatchNumbers;
    }

    // 배차 코드에 대한 검색
    private List<DispatchNumber> searchByDispatchCode(DispatchSearchRequest request, Users users, Center center,
                                                      LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        log.info("배송 코드 검색: {}", request.getSearchKeyword());
        if (isManager) {
            return dispatchNumberRepository.findByCenterAndUsersAndDispatchCodeAndLoadStartDateTimeBetween(
                    center, users, request.getSearchKeyword(), startDateTime, endDateTime);
        } else {
            return dispatchNumberRepository.findByCenterAndDispatchCodeAndLoadStartDateTimeBetween(
                    center, request.getSearchKeyword(), startDateTime, endDateTime);
        }
    }

    // 배차 명에 대한 검색
    private List<DispatchNumber> searchByDispatchName(DispatchSearchRequest request, Users users, Center center,
                                                      LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        log.info("배송 명 검색: {}", request.getSearchKeyword());
        if (isManager) {
            return dispatchNumberRepository.findByCenterAndUsersAndDispatchNameAndLoadStartDateTimeBetween(
                    center, users, request.getSearchKeyword(), startDateTime, endDateTime);
        } else {
            return dispatchNumberRepository.findByCenterAndDispatchNameAndLoadStartDateTimeBetween(
                    center, request.getSearchKeyword(), startDateTime, endDateTime);
        }
    }

    // 담당자에 대한 검색
    private List<DispatchNumber> searchByManager(DispatchSearchRequest request, Users users, Center center,
                                                 LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        Users manager = usersRepository.findByNameOrNull(request.getSearchKeyword());
        log.info("담당자 검색: {}", request.getSearchKeyword());
        // 자신 담당만 보기 & 다른 사람 검색 이면 빈 값 출력
        if (isManager && !users.equals(manager)) {
            return new ArrayList<>();
        }else {
            return dispatchNumberRepository.findByCenterAndUsersAndLoadStartDateTimeBetween(
                    center, manager, startDateTime, endDateTime);
        }
    }

    // 기사에 대한 검색
    private List<DispatchNumber> searchByDriverId(DispatchSearchRequest request, Users users, Center center,
                                                 LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        log.info("기사 검색: {}", request.getSearchKeyword());
        if (isManager) {
            return dispatchNumberRepository.findByCenterAndUsersAndSmNameAndLoadStartDateTimeBetween(
                    center, users, request.getSearchKeyword(), startDateTime, endDateTime);
        }else {
            return dispatchNumberRepository.findByCenterAndSmNameAndLoadStartDateTimeBetween(
                    center, request.getSearchKeyword(), startDateTime, endDateTime);
        }
    }

    // 검색이 없는 경우
    private List<DispatchNumber> searchByDefault(Users users, Center center, LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime, boolean isManager) {
        log.info("검색 옵션 없음");
        if (isManager) {
            return dispatchNumberRepository.findByCenterAndUsersAndLoadStartDateTimeBetween(
                    center, users, startDateTime, endDateTime);
        } else {
            return dispatchNumberRepository.findByCenterAndLoadStartDateTimeBetween(
                    center, startDateTime, endDateTime);
        }
    }

    // 진행률 계산
    private int calcProgress(int totalOrder, int completedOrder) {
        if (totalOrder == 0) {
            return 0;
        }
        return (int) Math.round((double) completedOrder / totalOrder * 100);
    }
}
