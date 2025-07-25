package com.springboot.transport.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.transport.entity.Contract;


public interface ContractRepository extends JpaRepository<Contract, Long> {
}