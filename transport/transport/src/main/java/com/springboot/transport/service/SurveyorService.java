package com.springboot.transport.service;   

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.transport.entity.Surveyor;
import com.springboot.transport.repository.SurveyorRepository;
        
@Service
public class SurveyorService {
    
    @Autowired
    private SurveyorRepository surveyorRepository;

  public List<Surveyor> getAvailableSurveyors() {
        return surveyorRepository.findAllActiveSurveyors();
    }

    public Surveyor getSurveyorById(Integer surveyorId) {
        return surveyorRepository.findById(surveyorId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên khảo sát với ID: " + surveyorId));
    }
}
