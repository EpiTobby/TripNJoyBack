package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.model.ReportModel;
import fr.tobby.tripnjoyback.model.ReportReason;
import fr.tobby.tripnjoyback.model.request.SubmitReportRequest;
import fr.tobby.tripnjoyback.model.request.UpdateReportRequest;
import fr.tobby.tripnjoyback.repository.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@DataJpaTest
public class ReportServiceTest {
    private static GenderEntity maleGender;
    private static GenderRepository genderRepository;
    private static CityRepository cityRepository;
    private static LanguageRepository languageRepository;
    private static CityEntity cityEntity;
    private static LanguageEntity languageEntity;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;

    private ReportService reportService;

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired StateRepository stateRepository,
                          @Autowired ApplicationContext context,
                          @Autowired CityRepository cityRepository, @Autowired LanguageRepository languageRepository)
    {
        maleGender = genderRepository.save(new GenderEntity("male"));
        ReportServiceTest.genderRepository = genderRepository;
        ReportServiceTest.cityRepository = cityRepository;
        ReportServiceTest.languageRepository = languageRepository;
        cityEntity = cityRepository.save(new CityEntity("Paris"));
        languageEntity = languageRepository.save(new LanguageEntity("French"));
        SpringContext.setContext(context);
    }

    @BeforeEach
    void setUp()
    {
        reportService = new ReportService(reportRepository, userRepository);
    }

    @NotNull
    private UserEntity anyUser() throws ParseException
    {
        return userRepository.save(UserEntity.builder()
                .firstname("Test")
                .lastname("1")
                .gender(maleGender)
                .email("test@1.com")
                .birthDate(new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2000").toInstant())
                .city(cityEntity)
                .confirmed(true)
                .language(languageEntity)
                .roles(List.of())
                .build());
    }

    @Test
    void submitReport() throws ParseException{
        UserEntity submitter = anyUser();
        UserEntity badUser = anyUser();
        ReportModel reportModel = reportService.submitReport(submitter.getId(), SubmitReportRequest.builder()
                .reportedUserId(badUser.getId())
                .reason(ReportReason.INNAPROPRIATE_BEHAVIOR)
                .details("Il fait caca partout")
                .build());
        Assertions.assertEquals(submitter.getId(), reportModel.getSubmitter().userId());
        Assertions.assertEquals(badUser.getId(), reportModel.getReportedUser().userId());
        Assertions.assertFalse(reportService.getBySubmitterId(submitter.getId()).isEmpty());
    }

    @Test
    void updateReport() throws ParseException{
        String expectedDetails = "Il fait pipi partout";
        UserEntity submitter = anyUser();
        UserEntity badUser = anyUser();
        long reportId = reportService.submitReport(submitter.getId(), SubmitReportRequest.builder()
                .reportedUserId(badUser.getId())
                .reason(ReportReason.INNAPROPRIATE_BEHAVIOR)
                .details("Il fait caca partout")
                .build()).getId();
        ReportModel reportModel = reportService.updateReport(reportId, UpdateReportRequest.builder()
                .reason(ReportReason.HYGIENE_PROBLEM)
                .details(expectedDetails)
                .build());
        Assertions.assertEquals(ReportReason.HYGIENE_PROBLEM, reportModel.getReason());
        Assertions.assertEquals(expectedDetails, reportModel.getDetails());
    }

    @Test
    void deleteReport() throws ParseException{
        UserEntity submitter = anyUser();
        UserEntity badUser = anyUser();
        long reportId = reportService.submitReport(submitter.getId(), SubmitReportRequest.builder()
                .reportedUserId(badUser.getId())
                .reason(ReportReason.INNAPROPRIATE_BEHAVIOR)
                .details("Il fait caca partout")
                .build()).getId();
        reportService.deleteReport(reportId);
        Assertions.assertTrue(reportService.getBySubmitterId(submitter.getId()).isEmpty());
    }

    @Test
    void getByReportedUser() throws ParseException{
        int numberOfReports = 10;
        UserEntity submitter = anyUser();
        UserEntity badUser = anyUser();
        for (int i = 0; i < numberOfReports; i++) {
            reportService.submitReport(submitter.getId(), SubmitReportRequest.builder()
                    .reportedUserId(badUser.getId())
                    .reason(ReportReason.INNAPROPRIATE_BEHAVIOR)
                    .details("Il fait caca partout")
                    .build());
        }
        Assertions.assertEquals(numberOfReports, reportService.getByReportedUserId(badUser.getId()).size());
    }
}
