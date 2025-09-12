package com.finalproject.springbackend.repository;

import com.finalproject.springbackend.entity.ResourceLevelFalse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceLevelFalseRepository extends JpaRepository<ResourceLevelFalse, Long> {
}
