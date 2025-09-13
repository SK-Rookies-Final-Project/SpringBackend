package com.finalproject.springbackend.service;

import com.finalproject.springbackend.entity.ResourceLevelFalse2;
import com.finalproject.springbackend.repository.ResourceLevelFalse2Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ResourceLevelFalse2Service {

    private final ResourceLevelFalse2Repository repo;
    /*
     * boolean str.isBlank() : str.length()==0 || str에 오직 모든 whitespace만 있으면 true
     * String str.strip() : 앞 뒤 whiteSpace 제거
     */

    /**
     * 파라미터 보정 메서드
     */
    //start, end 전체 보정
    private OffsetDateTime[] timesCorrection(OffsetDateTime start, OffsetDateTime end){
        start = ifStartIsNull(start);
        end = ifEndIsNull(end);
        return ifStartTimeAfterEndTime(start, end);
    }
    //start보다 end 시간이 더 이후일 때
    private OffsetDateTime[] ifStartTimeAfterEndTime(OffsetDateTime start, OffsetDateTime end){
        if (start.isAfter(end)){
            OffsetDateTime tmp = start;
            start = end;
            end = tmp;
        }
        return new OffsetDateTime[]{start, end};
    }

    //null 값일 경우 보정
    private OffsetDateTime ifEndIsNull(OffsetDateTime time){
        if(time==null){
            time = OffsetDateTime.now();
            return time;
        }
        else {
            return time;
        }
    }


    /**null 값일 경우 예외처리*/
    //start 가 null일경우
    private OffsetDateTime ifStartIsNull(OffsetDateTime time){
        if(time==null){
            throw new IllegalArgumentException(
                    "start 시간을 넣어주세요. \n" +
                    "형식: yyyy-MM-ddTHH:mm:ssZ"
            );
        } else {
            return time;
        }
    }

    //principal 값 보정
    private String correctionOfPrincipal(String principal){

        if(principal == null || principal.isBlank()) {
            throw new IllegalArgumentException("principal을 넣어주세요");
        }
        principal = principal.replaceAll("\\s+", "");
        if(!principal.startsWith("User:")) {
            principal = "User:"+principal;
        }
        return principal;

    }

    //resourceName 값 보정
    private String correctionOfResourceName(String resourceName) {


        if(resourceName == null || resourceName.isBlank()){
            throw new IllegalArgumentException("resourceName을 넣어주세요");
        }
        return resourceName.replaceAll("\\s+","");

    }

    private String correctionOfOperation(String operation){

        if(operation ==null || operation.isBlank()){
            throw new IllegalArgumentException("operation을 넣어주세요");
        }
        return operation.replaceAll("\\s+","");
    }

    private String correctionOfClientIp(String clientIp){
        if(clientIp == null || clientIp.isBlank()){
            throw new IllegalArgumentException("clientIp값을 넣어주세요");
        }
        return clientIp.replaceAll("\\s+", "");
    }



    /**
     * 레코드 조회 메서드
     */

    /** 시간으로만 찾기 */
    //시간 기준으로만 찾기
    public List<ResourceLevelFalse2> getTimesOnly (
            OffsetDateTime start,
            OffsetDateTime end
    ){

        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];


        return repo.findByEventTimeUTCBetweenOrderByEventTimeUTCAsc(start, end);
    }
    /** 시간 + 1개의 컬럼으로 레코드 조회*/
    //시간 + principal 컬럼으로 레코드 찾기
    public List<ResourceLevelFalse2> getTimeAndPrincipal(
            OffsetDateTime start,
            OffsetDateTime end,
            String principal
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];
        principal = correctionOfPrincipal(principal);

        return repo.findByEventTimeUTCBetweenAndPrincipalOrderByEventTimeUTCAsc(start, end, principal);
    }

    //시간 + resource_name 컬럼으로 레코드 찾기
    public List<ResourceLevelFalse2> getTimeAndResourceName(
            OffsetDateTime start,
            OffsetDateTime end,
            String resourceName
    ) {
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];
        resourceName = correctionOfResourceName(resourceName);

        return repo.findByEventTimeUTCBetweenAndResourceNameOrderByEventTimeUTCAsc(
                start, end, resourceName
        );
    }

    //시간 + operation
    public List<ResourceLevelFalse2> getTimeAndOperation(
            OffsetDateTime start, OffsetDateTime end, String operation
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        operation = correctionOfOperation(operation);

        return repo.findByEventTimeUTCBetweenAndOperationOrderByEventTimeUTCAsc(start, end, operation);
    }

    //시간 + client_ip
    public List<ResourceLevelFalse2> getTimeAndClientIp(
            OffsetDateTime start, OffsetDateTime end, String clientIp
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        clientIp = correctionOfClientIp(clientIp);

        return repo.findByEventTimeUTCBetweenAndClientIpOrderByEventTimeUTCAsc(start, end, clientIp);

    }

    /**시간 + 2가지 컬럼으로 찾기*/
    //시간 + principal, resource_name
    public List<ResourceLevelFalse2> getTimeAndPR(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String resourceName
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        principal = correctionOfPrincipal(principal);
        resourceName = correctionOfResourceName(resourceName);

        return repo.findByPR(start, end, principal, resourceName);
    }
    //시간 + principal, operation
    public List<ResourceLevelFalse2> getTimeAndPO(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String operation
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        principal = correctionOfPrincipal(principal);
        operation = correctionOfOperation(operation);

        return repo.findByPO(start, end, principal, operation);
    }

    //시간 + principal, clientIp
    public List<ResourceLevelFalse2> getTimeAndPC(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String clientIp
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        principal = correctionOfPrincipal(principal);
        clientIp = correctionOfClientIp(clientIp);

        return repo.findByPC(start, end, principal, clientIp);
    }
    
    
    //시간 + resource_name, operation
    public List<ResourceLevelFalse2> getTimeAndRO(
            OffsetDateTime start, OffsetDateTime end,
            String resourceName, String operation
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        resourceName = correctionOfResourceName(resourceName);
        operation = correctionOfOperation(operation);

        return repo.findByRO(start, end, resourceName, operation);
    }
    
    //시간 + resource_name + client_ip 으로 조회
    public List<ResourceLevelFalse2> getTimeAndRC (
            OffsetDateTime start, OffsetDateTime end,
            String resourceName, String clientIp
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        resourceName = correctionOfResourceName(resourceName);
        clientIp = correctionOfClientIp(clientIp);
        
        return repo.findByRC(start, end, resourceName, clientIp);
    }
    
    //시간 + operation, client_ip
    public List<ResourceLevelFalse2> getTimeAndOC (
            OffsetDateTime start, OffsetDateTime end,
            String operation, String clientIp
    ) {
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        operation = correctionOfOperation(operation);
        clientIp = correctionOfClientIp(clientIp);

        return repo.findByOC(start, end, operation, clientIp);
    }

    /**시간 + 3가지 컬럼으로 조회*/
    //시간 + principal, resource_name, operation
    public List<ResourceLevelFalse2> getTimeAndPRO(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String resourceName, String operation
    ) {
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        principal = correctionOfPrincipal(principal);
        resourceName = correctionOfResourceName(resourceName);
        operation = correctionOfOperation(operation);

        return repo.findByPRO(start, end, principal, resourceName, operation);
    }

    //시간 + principal + resource_name + client_ip
    public List<ResourceLevelFalse2> getTimeAndPRC(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String resourceName, String clientIp
    ) {
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        principal = correctionOfPrincipal(principal);
        resourceName = correctionOfResourceName(resourceName);
        clientIp = correctionOfClientIp(clientIp);

        return repo.findByPRC(start, end, principal, resourceName, clientIp);
    }

    //시간 + principal + operation + client_ip
    public List<ResourceLevelFalse2> getTimeAndPOC(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String operation, String clientIp
    ) {
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        principal = correctionOfPrincipal(principal);
        operation = correctionOfOperation(operation);
        clientIp = correctionOfClientIp(clientIp);

        return repo.findByPOC(start, end, principal, operation, clientIp);
    }

    //시간 + resource_name + operation + client_ip
    public List<ResourceLevelFalse2> getTimeAndROC(
            OffsetDateTime start, OffsetDateTime end,
            String resourceName, String operation, String clientIp
    ) {
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        resourceName = correctionOfResourceName(resourceName);
        operation = correctionOfOperation(operation);
        clientIp = correctionOfClientIp(clientIp);

        return repo.findByROC(start, end, resourceName, operation, clientIp);
    }

    /** 시간 + 4가지 컬럼으로 찾기 */
    //시간 + principal + resource_name + operation, client_ip
    public List<ResourceLevelFalse2> getTimeAndPROC(
            OffsetDateTime start, OffsetDateTime end,
            String principal, String resourceName, String operation, String clientIp
    ){
        OffsetDateTime[] times = timesCorrection(start, end);
        start = times[0];
        end = times[1];

        principal = correctionOfPrincipal(principal);
        resourceName = correctionOfResourceName(resourceName);
        operation = correctionOfOperation(operation);
        clientIp = correctionOfClientIp(clientIp);

        return repo.findByPROC(start, end, principal, resourceName, operation, clientIp);
    }
    //principal
    //resource_name
    //operation
    //client_ip
}
