package com.finalproject.springbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
class DbPingController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/db/ping")
    public Map<String, Object> ping() {
        Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return Map.of("ok", one != null && one == 1);
    }
}
