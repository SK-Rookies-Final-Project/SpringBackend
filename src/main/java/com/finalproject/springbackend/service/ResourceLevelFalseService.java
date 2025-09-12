package com.finalproject.springbackend.service;

import com.finalproject.springbackend.entity.ResourceLevelFalse;
import com.finalproject.springbackend.repository.ResourceLevelFalseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ResourceLevelFalseService {

    public ResourceLevelFalseRepository repo;

    public List<ResourceLevelFalse> getAll() {
        return repo.findAll();
    }
}
