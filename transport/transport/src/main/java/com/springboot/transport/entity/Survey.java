package com.springboot.transport.entity;
import jakarta.persistence.*;
import java.util.Date;

import lombok.Data;  
  @Data
@Entity
@Table(name = "survey")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Integer surveyId;

    @Column(name = "survey_date", columnDefinition = "DATETIME DEFAULT GETDATE() NOT NULL")
    private Date surveyDate;

    @Column(name = "survey_results", columnDefinition = "NVARCHAR(MAX)")
    private String surveyResults;

    @Column(name = "status", length = 20, columnDefinition = "NVARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'completed', 'cancelled'))")
    private String status;

    @ManyToOne
    @JoinColumn(name = "surveyor_id")
    private Surveyor surveyor;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private OrderRequest order;

    @ManyToOne
    @JoinColumn(name = "receptionist_id", nullable = false)
    private Receptionist receptionist;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private Date createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private Date updatedAt;
}