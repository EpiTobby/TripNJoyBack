package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.repository.SurveyRepository;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;

    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }
}
