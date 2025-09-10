package com.finalproject.springbackend.repository;

import com.finalproject.springbackend.entity.TestTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTableRepository extends JpaRepository<TestTable, Long> {
}
