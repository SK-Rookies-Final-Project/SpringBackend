package com.finalproject.springbackend.repository;

import com.finalproject.springbackend.entity.ResourceLevelFalse2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ResourceLevelFalse2Repository extends JpaRepository<ResourceLevelFalse2, String> {

    /** 각 레코드 별로 레코드 반환 */
    List<ResourceLevelFalse2> findByPrincipal(String principal);
    List<ResourceLevelFalse2> findByResourceName(String resourceName);
    List<ResourceLevelFalse2> findByOperation(String operation);
    List<ResourceLevelFalse2> findByClientIp(String clientIp);

    /** 시간 기준으로만 찾기*/
    //시간 기준으로만 찾기
    List<ResourceLevelFalse2> findByEventTimeUTCBetweenOrderByEventTimeUTCAsc(
            OffsetDateTime start, OffsetDateTime end
    );

    /** 시간과 하나의 컬럼으로 찾기*/
    //시간 + principal 기준으로 레코드 반환
    List<ResourceLevelFalse2> findByEventTimeUTCBetweenAndPrincipalOrderByEventTimeUTCAsc(
            OffsetDateTime start, OffsetDateTime end,  String principal
    );

    //시간 + resourceName 기준으로 레코드 반환
    List<ResourceLevelFalse2> findByEventTimeUTCBetweenAndResourceNameOrderByEventTimeUTCAsc(
            OffsetDateTime start, OffsetDateTime end, String resourceName
    );

    //시간 + operation 기준으로 레코드 반환
    List<ResourceLevelFalse2> findByEventTimeUTCBetweenAndOperationOrderByEventTimeUTCAsc(
            OffsetDateTime start, OffsetDateTime end, String operation
    );

    //시간 + client_ip 기준으로 레코드 반환
    List<ResourceLevelFalse2> findByEventTimeUTCBetweenAndClientIpOrderByEventTimeUTCAsc(
            OffsetDateTime start, OffsetDateTime end, String clientIp
    );

    /** 시간 + 2가지 컬럼으로 찾기 */
    //시간 + principal + resourceName
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.principal = :principal)
          AND (r.resourceName = :resourceName)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByPR(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("principal") String principal,
            @Param("resourceName") String resourceName
    );

    //시간 + principal + operation
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.principal = :principal)
          AND (r.operation = :operation)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByPO(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("principal") String principal,
            @Param("operation") String operation
    );

    //시간 + principal + clientIp
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.principal = :principal)
          AND (r.clientIp = :clientIp)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByPC(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("principal") String principal,
            @Param("clientIp") String clientIp
    );

    //시간 + resource_name + operation
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.resourceName = :resourceName)
          AND (r.operation = :operation)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByRO(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("resourceName") String resourceName,
            @Param("operation") String operation
    );

    //시간 + resource_name + client_ip
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.resourceName = :resourceName)
          AND (r.clientIp = :clientIp)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByRC(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("resourceName") String resourceName,
            @Param("clientIp") String clientIp
    );

    //시간 + operation + client_ip
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.operation = :operation)
          AND (r.clientIp = :clientIp)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByOC(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("operation") String operation,
            @Param("clientIp") String clientIp
    );

    /** 시간 + 3가지 컬럼으로 레코드 찾기 */
    //시간 + principal, resourceName, operation
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.principal = :principal)
          AND (r.resourceName = :resourceName)
          AND (r.operation = :operation)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByPRO(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("principal") String principal,
            @Param("resourceName") String resourceName,
            @Param("operation") String operation
    );

    //시간 + principal, resourceName, clientIp
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.principal = :principal)
          AND (r.resourceName = :resourceName)
          AND (r.clientIp = :clientIp)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByPRC(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("principal") String principal,
            @Param("resourceName") String resourceName,
            @Param("clientIp") String clientIp
    );

    //시간 + principal + operation += client_ip
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.principal = :principal)
          AND (r.operation = :operation)
          AND (r.clientIp = :clientIp)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByPOC(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("principal") String principal,
            @Param("operation") String operation,
            @Param("clientIp") String clientIp
    );



    //시간 + resourceName, operation, clientIp
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.resourceName = :resourceName)
          AND (r.operation = :operation)
          AND (r.clientIp = :clientIp)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByROC(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("resourceName") String resourceName,
            @Param("operation") String operation,
            @Param("clientIp") String clientIp
    );

    /** 시간 + 4가지 컬럼으로 레코드 찾기 */
    //시간 + principal + resource_name, operation, client_ip
    @Query("""
        SELECT r FROM ResourceLevelFalse2 r
        WHERE (r.eventTimeUTC >= :start) AND (r.eventTimeUTC <= :end)
          AND (r.principal = :principal)
          AND (r.resourceName = :resourceName)
          AND (r.operation = :operation)
          AND (r.clientIp = :clientIp)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByPROC(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("principal") String principal,
            @Param("resourceName") String resourceName,
            @Param("operation") String operation,
            @Param("clientIp") String clientIp
    );


}

//principal
//resource_name
//operation
//client_ip