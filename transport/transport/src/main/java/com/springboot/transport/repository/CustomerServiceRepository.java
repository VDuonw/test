package com.springboot.transport.repository;
import com.springboot.transport.entity.CustomerService;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerServiceRepository extends JpaRepository<CustomerService, Integer> {
    CustomerService findByServiceName(String serviceName);
    Optional<CustomerService> findById(Integer serviceId);
}
