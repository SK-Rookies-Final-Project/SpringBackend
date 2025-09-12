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

    /** 시간으로만 찾기 */
    //시간 기준으로만 찾기
    public List<ResourceLevelFalse2> getTimeStampWithUnAuthAccess (
            OffsetDateTime startTime,
            OffsetDateTime endTime
    ){

        OffsetDateTime start = startTime;
        OffsetDateTime end = endTime;

        if(end == null){
            end = OffsetDateTime.now();
        }

        return repo.findByEventTimeUTCBetweenOrderByEventTimeUTCAsc(start, end);
    }
    /** 시간 + 1개의 컬럼으로 레코드 조회*/
    //시간 + principal 컬럼으로 레코드 찾기
    public List<ResourceLevelFalse2> getTimeAndPrincipal(
            OffsetDateTime start,
            OffsetDateTime end,
            String principal
    ){
        if(end==null){
            end=OffsetDateTime.now();
        }

        return repo.findByEventTimeUTCBetweenAndPrincipalOrderByEventTimeUTCAsc(start, end, principal);
    }

    //시간 + principal 컬럼으로 레코드 찾기
    public List<ResourceLevelFalse2> getTimeAndResourceName(
            OffsetDateTime start,
            OffsetDateTime end,
            String resourceName
    ) {
        if (end == null) {
            end = OffsetDateTime.now();
        }

        return repo.findByEventTimeUTCBetweenAndResourceNameOrderByEventTimeUTCAsc(
                start, end, resourceName
        );
    }

    //시간 + resource_name
    public List<ResourceLevelFalse2> getTimeAndOperation(
            OffsetDateTime start, OffsetDateTime end, String operation
    ){
        if(end==null){ end=OffsetDateTime.now();}
        return repo.findByEventTimeUTCBetweenAndOperationOrderByEventTimeUTCAsc(start, end, operation);
    }

    //시간 + client_ip
    public List<ResourceLevelFalse2> getTimeAndClientIp(
            OffsetDateTime start, OffsetDateTime end, String clientIp
    ){
        if(end==null){ end=OffsetDateTime.now(); }
        return repo.findByEventTimeUTCBetweenAndClientIpOrderByEventTimeUTCAsc(start, end, clientIp);

    }

    /**시간 + 2가지 컬럼으로 찾기*/
    //시간 + principal, resource_name
    public List<ResourceLevelFalse2> getTimeAndPR(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String resourceName
    ){
        if(end==null) { end=OffsetDateTime.now(); }
        return repo.findByPR(start, end, principal, resourceName);
    }
    //시간 + principal, operation
    public List<ResourceLevelFalse2> getTimeAndPO(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String operation
    ){
        if(end==null) { end=OffsetDateTime.now(); }
        return repo.findByPO(start, end, principal, operation);
    }
    //시간 + principal, clientIp
    public List<ResourceLevelFalse2> getTimeAndPC(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String clientIp
    ){
        if(end==null) {end=OffsetDateTime.now();}
        return repo.findByPC(start, end, principal, clientIp);
    }
    //시간 + resource_name, operation
    public List<ResourceLevelFalse2> getTimeAndRO(
            OffsetDateTime start, OffsetDateTime end,
            String resourceName, String operation
    ){
        if(end==null) { end = OffsetDateTime.now(); }
        return repo.findByRO(start, end, resourceName, operation);
    }
    //시간 + operation, client_ip
    public List<ResourceLevelFalse2> getTimeAndOC (
            OffsetDateTime start, OffsetDateTime end,
            String operation, String clientIp
    ) {
        if(end==null) end=OffsetDateTime.now();
        return repo.findByOC(start, end, operation, clientIp);
    }

    /**시간 + 3가지 컬럼으로 조회*/
    //시간 + principal, resource_name, operation
    public List<ResourceLevelFalse2> getTimeAndPRO(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String resourceName, String operation
    ) {
        if(end==null){ end=OffsetDateTime.now(); }
        return repo.findByPRO(start, end, principal, resourceName, operation);
    }

    //시간 + principal + resource_name + client_ip
    public List<ResourceLevelFalse2> getTimeAndPRC(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String resourceName, String clientIp
    ) {
        if(end==null){ end=OffsetDateTime.now(); }
        return repo.findByPRC(start, end, principal, resourceName, clientIp);
    }

    //시간 + principal + operation + client_ip
    public List<ResourceLevelFalse2> getTimeAndPOC(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String operation, String clientIp
    ) {
        if(end==null){ end=OffsetDateTime.now(); }
        return repo.findByPRC(start, end, principal, operation, clientIp);
    }

    //시간 + resource_name + operation + client_ip
    public List<ResourceLevelFalse2> getTimeAndROC(
            OffsetDateTime start, OffsetDateTime end,
            String resourceName, String operation, String clientIp
    ) {
        if(end==null){ end=OffsetDateTime.now(); }
        return repo.findByROC(start, end, resourceName, operation, clientIp);
    }

    /** 시간 + 4가지 컬럼으로 찾기 */
    //시간 + principal + resource_name + operation, client_ip
    public List<ResourceLevelFalse2> getTimeAndPROC(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String resourceName, String operation, String clientIp
    ){
        if(end==null) { end=OffsetDateTime.now(); }
        return repo.findByPROC(start, end, principal, resourceName, operation, clientIp);
    }
    //principal
    //resource_name
    //operation
    //client_ip
}
