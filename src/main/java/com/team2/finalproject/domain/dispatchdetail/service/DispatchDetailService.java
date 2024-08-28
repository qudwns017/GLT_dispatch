package com.team2.finalproject.domain.dispatchdetail.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchdetail.exception.DispatchDetailErrorCode;
import com.team2.finalproject.domain.dispatchdetail.exception.DispatchDetailException;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.repository.DispatchDetailRepository;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchDetailService {

    private final DispatchDetailRepository dispatchDetailRepository;

    private final DispatchRepository dispatchRepository;

    public void cancelDispatchDetailList(List<Long> dispatchDetailIdList, Long centerId){
        List<DispatchDetail> dispatchDetailList = dispatchDetailRepository.findWithTransportOrderAndCenterByIdIn(dispatchDetailIdList);

        if(dispatchDetailList.size() != dispatchDetailIdList.size()){
            throw new DispatchDetailException(DispatchDetailErrorCode.INVALID_IN_REQUEST);
        }

        validateDispatchDetailList(dispatchDetailList,centerId);

        dispatchDetailList.forEach(
            dispatchDetail -> {
                dispatchDetail.cancel();
                dispatchDetail.getTransportOrder().pend();
                dispatchDetailRepository.save(dispatchDetail);
            }
        );

        Dispatch dispatch = dispatchDetailList.get(0).getDispatch();
        dispatch.minusOrderCount(dispatchDetailList.size());
        dispatchRepository.save(dispatch);
    }

    private void validateDispatchDetailList(List<DispatchDetail> dispatchDetailList, Long centerId) {
        // 모든 DispatchDetail 검증
        boolean allMatch = dispatchDetailList.stream()
            .allMatch(dispatchDetail -> {
                TransportOrder transportOrder = dispatchDetail.getTransportOrder();
                if (transportOrder == null) {
                    throw new DispatchDetailException(DispatchDetailErrorCode.NOT_FOUND_TRANSPORT_ORDER_IN_DISPATCH_DETAIL);
                }
                Center center = transportOrder.getCenter();
                return center != null && center.getId().equals(centerId);
            });

        if (!allMatch) {
            throw new DispatchDetailException(DispatchDetailErrorCode.NOT_MATCH_CENTER_AND_DISPATCH_DETAIL);
        }
    }
}
