package com.springboot.transport.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import com.springboot.transport.entity.Survey;
import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {
    List<Survey> findByReceptionist_ReceptionistId(Integer receptionistId);
    boolean existsByOrder_OrderId(Integer orderId);
}