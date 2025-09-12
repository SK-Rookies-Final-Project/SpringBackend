package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.entity.SystemLevelFalse;
import com.finalproject.springbackend.service.SystemLevelFalseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/db")
@RequiredArgsConstructor
public class GetRecordController {
    
    private final SystemLevelFalseService slfService;

    @GetMapping("/system_level_false")
    public ResponseEntity<List<SystemLevelFalse>> getAllSystemLevelFalse(){
        List<SystemLevelFalse> slf = slfService.getAll();
        return ResponseEntity.ok(slf);
    }

}
