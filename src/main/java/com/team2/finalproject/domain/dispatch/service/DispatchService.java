package com.team2.finalproject.domain.dispatch.service;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.IssueRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DispatchService {
    private final DispatchRepository dispatchRepository;
    private final DispatchNumberRepository dispatchNumberRepository;
    private final SmRepository smRepository;
    private final TransportOrderRepository transportOrderRepository;
    private final DispatchDetailRepository dispatchDetailRepository;

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
