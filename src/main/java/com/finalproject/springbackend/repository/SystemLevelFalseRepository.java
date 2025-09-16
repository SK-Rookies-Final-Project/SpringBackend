package com.finalproject.springbackend.repository;

import com.finalproject.springbackend.entity.SystemLevelFalse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLevelFalseRepository extends JpaRepository<SystemLevelFalse, String> {

}
