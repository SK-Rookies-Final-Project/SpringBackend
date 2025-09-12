package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.entity.*;
import com.finalproject.springbackend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/db")
@RequiredArgsConstructor
public class GetRecordController {

    private final SystemLevelFalseService slfService;
    private final Certified2TimeService c2tService;
    private final CertifiedNotMoveService cnmService;
    private final ResourceLevelFalseService rlfService;
    private final ResourceLevelFalse2Service rlf2Service;

    /**
     * 테이블 레코드 전체를 가져오는 엔드포인트 ( /<레코드>/all )
     */
    //system-level-false 테이블의 레코드 전체를 가져오는 클래스
    @GetMapping("/system_level_false/all")
    public ResponseEntity<List<SystemLevelFalse>> getAllSLF() {
        List<SystemLevelFalse> slf = slfService.getAll();
        return ResponseEntity.ok(slf);
    }

    //certified-2-time 테이블의 레코드 전체를 가져오는 클래스
    @GetMapping("/certified_2_time/all")
    public ResponseEntity<List<Certified2Time>> getAllC2T() {
        List<Certified2Time> c2t = c2tService.getAll();
        return ResponseEntity.ok(c2t);
    }

    //certified-not-move 테이블의 레코드 전체를 가져오는 클래스
    @GetMapping("/certified_not_move/all")
    public ResponseEntity<List<CertifiedNotMove>> getAllCNM() {
        List<CertifiedNotMove> cnm = cnmService.getAll();

        return ResponseEntity.ok(cnm);
    }

    //resource_level_false 테이블 레코드 전체를 가져오는 클래스
    @GetMapping("/resource_level_false/all")
    public ResponseEntity<List<ResourceLevelFalse>> getAllRLF() {
        List<ResourceLevelFalse> rlf = rlfService.getAll();

        return ResponseEntity.ok(rlf);
    }


    /**
     * 시간 기준 resource-level-false 테이블의 레코드 반환
     * http://localhost:8080/api/db/resource_level_false?startTime=2025-09-12T00:12:43Z&endTime=2025-09-12T12:34:56Z로
     * GET 요청을 보내면 startTime과 endTime 사이의 레코드 반환
     * startTime 데이터 형식: yyyy-mm-ddThh:mm:ssZ
     * endTime 데이터 형식: yyyy-mm-ddThh:mm:ssZ
     *  yyyy-mm-dd 뒤에 T, hh:mm:ss 뒤에 Z를 붙여야 함
     */
    @GetMapping("/resource_level_false")
    public ResponseEntity<List<ResourceLevelFalse2>> getStartEndTime(
            @RequestParam OffsetDateTime startTime,
            @RequestParam OffsetDateTime endTime
    ){
        List<ResourceLevelFalse2> rlf2 = rlf2Service
                .getTimeStampWithUnAuthAccess(startTime, endTime);

        return ResponseEntity.ok(rlf2);
    }
}
