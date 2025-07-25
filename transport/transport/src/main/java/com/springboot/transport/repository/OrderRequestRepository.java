package com.springboot.transport.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.transport.entity.OrderRequest;        

import java.util.List;

public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {
    List<OrderRequest> findByStatus(String status);
    OrderRequest  findByOrderId(Long orderId);
}
