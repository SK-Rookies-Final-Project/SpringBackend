package com.finalproject.springbackend.service;

import com.finalproject.springbackend.entity.CertifiedNotMove;
import com.finalproject.springbackend.repository.CertifiedNotMoveRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CertifiedNotMoveService {

    public CertifiedNotMoveRepository repo;

    public List<CertifiedNotMove> getAll() {
        return repo.findAll();
    }

}
