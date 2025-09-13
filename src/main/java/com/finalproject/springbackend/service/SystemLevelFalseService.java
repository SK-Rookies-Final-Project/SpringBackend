package com.finalproject.springbackend.service;

import com.finalproject.springbackend.entity.SystemLevelFalse;
import com.finalproject.springbackend.repository.SystemLevelFalseRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SystemLevelFalseService {

    public SystemLevelFalseRepository repo;

    //system-level-false 테이블의 모든 레코드 값 가져오기
    public List<SystemLevelFalse> getAll() {
        return repo.findAll();
    }
}
