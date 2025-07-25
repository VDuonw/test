package com.springboot.transport.service;
import com.springboot.transport.entity.Survey;  
import com.springboot.transport.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    public Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    public List<Survey> getSurveysByReceptionist(Integer receptionistId) {
        return surveyRepository.findByReceptionist_ReceptionistId(receptionistId);
    }

    public Survey getSurveyById(Integer surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khảo sát với ID: " + surveyId));
    }

    public Survey updateSurvey(Integer surveyId, String surveyResults, String status) {
        Survey survey = getSurveyById(surveyId);
        
        if (surveyResults != null) {
            survey.setSurveyResults(surveyResults);
        }
        
        if (status != null) {
            if (!List.of("pending", "completed", "cancelled").contains(status)) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
            }
            survey.setStatus(status);
        }
        
        survey.setUpdatedAt(new java.util.Date());
        return surveyRepository.save(survey);
    }

    public boolean existsSurveyForOrder(Integer orderId) {
        return surveyRepository.existsByOrder_OrderId(orderId);
    }
}