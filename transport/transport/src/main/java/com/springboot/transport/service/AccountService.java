package com.springboot.transport.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springboot.transport.entity.Account;
import com.springboot.transport.repository.AccountRepository;



@Service
public class AccountService implements UserDetailsService{

    @Autowired
    private AccountRepository accountRepository;

    public Account getAccountByUserName(String username) {
        return accountRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Account not found"));
       
    }


     public Long getCurrentUserRealId() {
        // Lấy username từ security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Tìm account theo username
        Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));

        // Trả về referenceId
        return account.getReferenceId();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(account.getUsername())
                   .password(account.getPassword())
                   .roles(account.getRole()) // Spring Security sẽ dùng ROLE_RECEPTIONIST, ROLE_MANAGER
                   .build();
    }

   
} 
