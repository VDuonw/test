package com.springboot.transport.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;
    
    private String contractCode;
    private Date contractDate;
    private String terms;
    private String status;
    
    @OneToOne
    @JoinColumn(name = "order_id")
    private OrderRequest orderRequest;
    
    @ManyToOne
    @JoinColumn(name = "logistic_id")
    private LogisticManager logisticManager;
    
    private Date createdAt;
    private Date updatedAt;
}