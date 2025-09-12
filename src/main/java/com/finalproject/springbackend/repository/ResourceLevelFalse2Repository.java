package com.finalproject.springbackend.repository;

import com.finalproject.springbackend.entity.ResourceLevelFalse2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ResourceLevelFalse2Repository extends JpaRepository<ResourceLevelFalse2, String> {
    //시간 기준으로만 찾기
    @Query("""
        SELECT r
        FROM ResourceLevelFalse2 r
        WHERE (:start <= r.eventTimeUTC) 
          and (:end >= r.eventTimeUTC)
        ORDER BY r.eventTimeUTC ASC
    """)
    List<ResourceLevelFalse2> findByEventTimeUTCRange(
            OffsetDateTime start, OffsetDateTime end
    );

//    List<ResourceLevelFalse2> findByEventTimeUTCAnd

}

//principal
//resource_name
//client_ip
//operation
