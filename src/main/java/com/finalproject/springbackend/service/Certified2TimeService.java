package com.finalproject.springbackend.service;

import com.finalproject.springbackend.entity.Certified2Time;
import com.finalproject.springbackend.repository.Certified2TimeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class Certified2TimeService {

    public Certified2TimeRepository repo;

    public List<Certified2Time> getAll() {
        return repo.findAll();
    }
}
