package com.finalproject.springbackend.repository;

import com.finalproject.springbackend.entity.Certified2Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Certified2TimeRepository extends JpaRepository<Certified2Time, String>{
}
