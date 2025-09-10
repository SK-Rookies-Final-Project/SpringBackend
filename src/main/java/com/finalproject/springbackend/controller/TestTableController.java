package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.entity.TestTable;
import com.finalproject.springbackend.repository.TestTableRepository;
import com.finalproject.springbackend.service.TestTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/db")
@RequiredArgsConstructor
public class TestTableController {

    private final TestTableService ttservice;

    @GetMapping("/show_test_table")
    public List<TestTable> showTestTable() {
        return ttservice.getShowTestTable1();
    }

    @PostMapping("/test_insert")
    public ResponseEntity<TestTable> create(@RequestParam String str){
        TestTable tt=ttservice.create(str);
        return ResponseEntity.ok(tt);
    }

}
