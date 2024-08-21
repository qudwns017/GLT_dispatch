package com.team2.finalproject.domain.dispatch.service;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchSearchRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchSearchResponse;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DispatchService {
    private final DispatchRepository dispatchRepository;
    private final DispatchNumberRepository dispatchNumberRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public DispatchSearchResponse searchDispatches(DispatchSearchRequest request, Long userId) {
        Users currentUser = usersRepository.findByIdOrThrow(userId);
        Long centerId = currentUser.getCenterId();
        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        // 검색 기간, 검색 옵션, 담당자 체크 여부에 따른 필터
        List<DispatchNumber> dispatchNumbers =
                searchDispatchNumbers(request, userId, centerId, startDateTime, endDateTime);

        // 각 상태에 해당하는 개수 구하기
        long inTransit = dispatchNumbers.stream().filter(d -> d.getStatus() == DispatchStatus.IN_TRANSIT).count();
        long waiting = dispatchNumbers.stream().filter(d -> d.getStatus() == DispatchStatus.WAITING).count();
        long completed = dispatchNumbers.stream().filter(d -> d.getStatus() == DispatchStatus.COMPLETED).count();

        // 검색을 원하는 status
        DispatchStatus status = request.getStatus();

        // DispatchNumber ID 리스트 추출
        List<Long> dispatchNumberIds = dispatchNumbers.stream()
                .map(DispatchNumber::getId)
                .collect(Collectors.toList());

        // Dispatch 리스트를 한번에 조회하고, Map으로 그룹핑
        Map<Long, List<Dispatch>> dispatchMap = dispatchRepository.findByDispatchNumberIdIn(dispatchNumberIds).stream()
                .collect(Collectors.groupingBy(Dispatch::getDispatchNumberId));


        // 주생 중, 주행 대기, 주행 완료에 따른 필터
        // 기사 수, 전체 주문 수, 완료 주문 수 구하기
        List<DispatchSearchResponse.DispatchResult> results = dispatchNumbers.stream()
                .filter(d -> d.getStatus().equals(status))
                .map(dispatchNumber -> {
                    List<Dispatch> dispatchList = dispatchMap.get(dispatchNumber.getId());
                    int smNum = dispatchList.size();
                    int totalOrder = dispatchList.stream().mapToInt(Dispatch::getDeliveryOrderCount).sum();
                    int completedOrder = dispatchList.stream().mapToInt(Dispatch::getCompletedOrderCount).sum();

                    // 대기 중이면 0%, 배송 완료면 100%, 그 외에는 진행율 계산
                    int progress = 0;
                    if (status.equals(DispatchStatus.COMPLETED)) {
                        progress = 100;
                    } else if (status.equals(DispatchStatus.IN_TRANSIT)) {
                        progress = totalOrder == 0 ? 0 : calcProgress(totalOrder, completedOrder);
                    }

                    return DispatchSearchResponse.DispatchResult.builder()
                            .progress(progress)
                            .dispatchCode(dispatchNumber.getDispatchCode())
                            .dispatchName(dispatchNumber.getDispatchName())
                            .startDateTime(dispatchNumber.getLoadStartDateTime())
                            .totalOrder(totalOrder)
                            .smNum(smNum)
                            .manager(usersRepository.findNameByIdOrThrow(dispatchNumber.getId()))
                            .build();
                })
                .collect(Collectors.toList());

        return DispatchSearchResponse.builder()
                .inProgress((int)inTransit)
                .waiting((int)waiting)
                .completed((int)completed)
                .results(results)
                .build();

    }

    // 검색 기간, 검색 옵션, 담당자 체크 여부에 따른 필터
    private List<DispatchNumber> searchDispatchNumbers(DispatchSearchRequest request, Long userId, Long centerId,
                                                       LocalDateTime startDateTime, LocalDateTime endDateTime) {

        List<DispatchNumber> dispatchNumbers = new ArrayList<>();
        boolean isManager = request.getIsManager(); // 담당자 여부
        // 검색 옵션에 다른 검색 (검색창 검색)
        if(request.getSearchOption() != null) {
            // 배차 코드 검색
            if(request.getSearchOption().equals("dispatchCode")) {
                dispatchNumbers = searchByDispatchCode(request, userId, centerId,
                        startDateTime, endDateTime, isManager);
                // 배차 명 검색
            }else if(request.getSearchOption().equals("dispatchName")) {
                dispatchNumbers = searchByDispatchName(request, userId, centerId,
                        startDateTime, endDateTime, isManager);
                // 배차 담당자 검색
            }else if(request.getSearchOption().equals("adminId")){
                dispatchNumbers = searchByAdminId(request, userId, centerId,
                        startDateTime, endDateTime, isManager);
            }
        }else{
            dispatchNumbers = searchByDefault(userId, centerId,
                    startDateTime, endDateTime, isManager);
        }
        return dispatchNumbers;
    }

    // 배송 코드에 대한 검색
    private List<DispatchNumber> searchByDispatchCode(DispatchSearchRequest request, Long userId, Long centerId,
                                                      LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        if (isManager) {
            return dispatchNumberRepository.findByCenterIdAndAdminIdAndDispatchCodeAndLoadStartDateTimeBetween(
                    centerId, userId, request.getSearchKeyword(), startDateTime, endDateTime);
        } else {
            return dispatchNumberRepository.findByCenterIdAndDispatchCodeAndLoadStartDateTimeBetween(
                    centerId, request.getSearchKeyword(), startDateTime, endDateTime);
        }
    }

    // 배송 명에 대한 검색
    private List<DispatchNumber> searchByDispatchName(DispatchSearchRequest request, Long userId, Long centerId,
                                                      LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        if (isManager) {
            return dispatchNumberRepository.findByCenterIdAndAdminIdAndDispatchNameAndLoadStartDateTimeBetween(
                    centerId, userId, request.getSearchKeyword(), startDateTime, endDateTime);
        } else {
            return dispatchNumberRepository.findByCenterIdAndDispatchNameAndLoadStartDateTimeBetween(
                    centerId, request.getSearchKeyword(), startDateTime, endDateTime);
        }
    }

    // 담당자에 대한 검색
    private List<DispatchNumber> searchByAdminId(DispatchSearchRequest request, Long userId, Long centerId,
                                                 LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isManager) {
        if (isManager && userId.toString().equals(request.getSearchKeyword())) {
            return dispatchNumberRepository.findByCenterIdAndAdminIdAndLoadStartDateTimeBetween(
                    centerId, userId, startDateTime, endDateTime);
        }
        return null;
    }

    // 검색이 없는 경우
    private List<DispatchNumber> searchByDefault(Long userId, Long centerId, LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime, boolean isManager) {
        if (isManager) {
            return dispatchNumberRepository.findByCenterIdAndAdminIdAndLoadStartDateTimeBetween(
                    centerId, userId, startDateTime, endDateTime);
        } else {
            return dispatchNumberRepository.findByCenterIdAndLoadStartDateTimeBetween(
                    centerId, startDateTime, endDateTime);
        }
    }

    // 진행룰 계산
    private int calcProgress (int totalOrder, int completedOrder) {

        if (totalOrder == 0 && completedOrder == 0) {
            return 0;
        }

        return (int) Math.round((double) totalOrder / completedOrder * 100);
    }
}
