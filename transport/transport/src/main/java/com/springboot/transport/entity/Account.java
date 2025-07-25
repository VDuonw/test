package com.springboot.transport.entity;
import java.sql.Date;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "account")    
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;
    
    @Column(unique = true)
    private String username;
    private String password;
    private String role;
    private Long referenceId;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}