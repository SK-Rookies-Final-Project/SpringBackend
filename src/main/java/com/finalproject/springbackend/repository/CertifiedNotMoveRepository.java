package com.finalproject.springbackend.repository;

import com.finalproject.springbackend.entity.CertifiedNotMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertifiedNotMoveRepository extends JpaRepository<CertifiedNotMove, String>{
}
