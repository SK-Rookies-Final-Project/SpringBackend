package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.entity.*;
import com.finalproject.springbackend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/db")
@RequiredArgsConstructor
public class GetRecordController {

    private final SystemLevelFalseService slfService;
    private final Certified2TimeService c2tService;
    private final CertifiedNotMoveService cnmService;
    private final ResourceLevelFalseService rlf2Service;

    /**
     * 테이블 레코드 전체를 가져오는 엔드포인트 ( /<레코드>/all )
     */
    //system-level-false 테이블의 레코드 전체를 가져오는 클래스
//    @GetMapping("/system_level_false/all")
//    public ResponseEntity<List<SystemLevelFalse>> getAllSLF() {
//        List<SystemLevelFalse> slf = slfService.getAll();
//        return ResponseEntity.ok(slf);
//    }

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


}