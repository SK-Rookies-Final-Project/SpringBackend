package com.finalproject.springbackend.service;

import com.finalproject.springbackend.entity.TestTable;
import com.finalproject.springbackend.repository.TestTableRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestTableService {

    private final TestTableRepository repo;

    public List<TestTable> getShowTestTable1(){
        return repo.findAll();
    }


    @Transactional
    public TestTable create(String str) {
        TestTable tt = TestTable.builder()
                .name(str)
                .build();
        return repo.save(tt);
    }



}
