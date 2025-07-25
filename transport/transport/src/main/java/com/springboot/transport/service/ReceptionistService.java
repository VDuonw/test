package com.springboot.transport.service;
import com.springboot.transport.entity.Account;
import com.springboot.transport.entity.Receptionist;
import com.springboot.transport.repository.AccountRepository;
import com.springboot.transport.repository.ReceptionistRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReceptionistService {
    
    @Autowired
    private ReceptionistRepository receptionistRepository;

    @Autowired
    private AccountRepository accountRepository;

 


    public Optional<Receptionist> findByEmail(String email) {
        return receptionistRepository.findByEmail(email);
    }


  
    public Receptionist getCurrentReceptionist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username)
                .orElse(null);
                
        if (account == null || !"RECEPTIONIST".equals(account.getRole())) {
            return null;
        }
        
        return receptionistRepository.findById(account.getReferenceId().intValue())
                .orElse(null);
    }
} 