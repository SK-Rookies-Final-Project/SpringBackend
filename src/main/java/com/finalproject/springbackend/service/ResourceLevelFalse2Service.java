package com.finalproject.springbackend.service;

import com.finalproject.springbackend.entity.ResourceLevelFalse;
import com.finalproject.springbackend.entity.ResourceLevelFalse2;
import com.finalproject.springbackend.repository.ResourceLevelFalse2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceLevelFalse2Service {

    private final ResourceLevelFalse2Repository repo;

    public List<ResourceLevelFalse2> getTimeStampWithUnAuthAccess (
            OffsetDateTime startTime,
            OffsetDateTime endTime
    ){

        OffsetDateTime start = startTime;
        OffsetDateTime end = endTime;

        if(end == null){
            end = OffsetDateTime.now();
        }

        return repo.findByEventTimeUTCRange(start, end);
    }

}
