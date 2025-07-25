package com.springboot.transport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.transport.entity.Contract;
import com.springboot.transport.entity.OrderRequest;
import com.springboot.transport.repository.ContractRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    public Contract createContractFromOrder(OrderRequest order, String terms) {
        Contract contract = new Contract();
        contract.setContractCode("HD-" + UUID.randomUUID().toString().substring(0, 8));
        contract.setContractDate(new Date()); 
        contract.setTerms(terms);
        contract.setStatus("active");
        contract.setOrderRequest(order);
        contract.setCreatedAt(new Date()); 
        contract.setUpdatedAt(new Date()); 

        return contractRepository.save(contract);
    }
}