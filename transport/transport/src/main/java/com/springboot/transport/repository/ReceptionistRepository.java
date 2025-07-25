package com.springboot.transport.repository;
import com.springboot.transport.entity.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist, Integer> {
    Optional<Receptionist> findByEmail(String email);
    Optional<Receptionist> findById(Integer receptionistId);
} 