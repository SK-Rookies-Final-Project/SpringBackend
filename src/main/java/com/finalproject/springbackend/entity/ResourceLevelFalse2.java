package com.finalproject.springbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"resource-level-false2\"")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceLevelFalse2 {

    @Id @Column(columnDefinition = "text")
    private String id;      // 기본키

    @Column(name = "event_time_utc", columnDefinition = "timestamptz", nullable = false)
    private OffsetDateTime eventTimeUTC;    // 특정 이벤트가 일어 난 시간

    //지금은 해당 컬럼의 자료형이 text라 나중에 주석 해제하고 사용하기
//    @Column(name = "event_time_kst", columnDefinition = "timestamptz", nullable = false)
//    private OffsetDateTime eventTimeKST;    // 특정 이벤트가 일어 난 한국 시간(UTC +9)
    @Column(name = "event_time_kst", columnDefinition = "text", nullable = false)
    private String eventTimeKST;    // 특정 이벤트가 일어 난 한국 시간(UTC +9)


    @Column(name = "process_time_utc", columnDefinition = "timestamptz", nullable = false)
    private OffsetDateTime processTimeUTC;  // Flink로 특정 이벤트가 변경 된 시간

    //지금은 해당 컬럼의 자료형이 text라 나중에 주석 해제하고 사용하기
//    @Column(name = "process_time_kst", columnDefinition = "timestamptz", nullable = false)
//    private OffsetDateTime processTimeKST;  // Flink로 특정 이벤트가 변경 된 한국 시간 (UTC +9)
    @Column(name = "process_time_kst", columnDefinition = "text", nullable = false)
    private String processTimeKST;  // Flink로 특정 이벤트가 변경 된 한국 시간 (UTC +9)


    @Column(name = "principal", columnDefinition = "text")
    private String principal;   // 유저 이름

    @Column(name = "role", columnDefinition = "text")
    private String role;        // 유저 권한

    @Column(name = "operation", columnDefinition = "text")
    private String operation;   // 특정 유저가 해당 리소스에 권한이 없는 어떤 행동을 했는지

    @Column(name = "action", columnDefinition="text")
    private String action;      // Kafka-Cluster 기준 produce(데이터 보내기), consume(데이터 구독 후 가져오기), describe(데이터 읽기)

    @Column(name = "method_name", columnDefinition="text")
    private String methodName;  // kafka.Produce, MDS.Authorize, kafka.Metadata 등등 개많음

    @Column(name = "resource_name", columnDefinition="text")
    private String resourceName;    // 어디 리소스에서 권한이 없는 행동을 했는지 (ex: audit-topic)

    @Column(name = "client_id", columnDefinition="text")
    private String clientId;        //신경 안써도 됨

    @Column(name = "client_ip", columnDefinition="text")
    private String clientIp;        //어떤 IP에서 비인가 접근을 시도했는지

}
