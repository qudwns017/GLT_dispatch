package com.team2.finalproject.domain.center.service;

import com.team2.finalproject.domain.center.model.dto.request.CenterRequest;
import com.team2.finalproject.domain.center.model.dto.response.CenterResponse;
import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CenterService {

    private final CenterRepository centerRepository;

    public CenterResponse getCenter(long centerId) {
        Center centerEntity = centerRepository.findByCenterByCenterIdOrThrow(centerId);
        return CenterResponse.of(centerEntity);
    }

    public CenterResponse addCenter(CenterRequest request) {
        Center center = centerRepository.save(CenterRequest.toEntity(request));
        return CenterResponse.of(center);
    }
}
