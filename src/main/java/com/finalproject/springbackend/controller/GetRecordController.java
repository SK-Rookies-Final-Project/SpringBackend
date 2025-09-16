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


}