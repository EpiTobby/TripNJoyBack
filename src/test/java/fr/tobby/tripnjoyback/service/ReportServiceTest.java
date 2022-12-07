package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.PromStats;
import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.GenderEntity;
import fr.tobby.tripnjoyback.entity.LanguageEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.model.ReportModel;
import fr.tobby.tripnjoyback.model.ReportReason;
import fr.tobby.tripnjoyback.model.request.SubmitReportRequest;
import fr.tobby.tripnjoyback.model.request.UpdateReportRequest;
import fr.tobby.tripnjoyback.repository.*;
import io.prometheus.client.Gauge;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
class ReportServiceTest {
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
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired ApplicationContext context,
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
        IdCheckerService idCheckerService = mock(IdCheckerService.class);
        when(idCheckerService.getCurrentUserId()).thenReturn(1L);
        PromStats promStats = mock(PromStats.class);
        when(promStats.getReportCount()).thenReturn(mock(Gauge.class));
        reportService = new ReportService(reportRepository, userRepository, idCheckerService, promStats);
    }

    @AfterEach
    void tearDown()
    {
        reportRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    static void afterAll()
    {
        genderRepository.deleteAll();
        cityRepository.deleteAll();
        languageRepository.deleteAll();
    }

    @NotNull
    private UserEntity anyUser(String email) throws ParseException
    {
        return userRepository.save(UserEntity.builder()
                .firstname("Test")
                .lastname("1")
                .gender(maleGender)
                .email(email)
                .birthDate(new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2000").toInstant())
                .city(cityEntity)
                .confirmed(true)
                .language(languageEntity)
                .roles(List.of())
                .build());
    }

    @Test
    void submitReportTest() throws ParseException{
        String submitterEmail = "submitter@gmail.com";
        UserEntity submitter = anyUser(submitterEmail);
        UserEntity badUser = anyUser("user@gmail.com");
        ReportModel reportModel = reportService.submitReport(submitterEmail, SubmitReportRequest.builder()
                .reportedUserId(badUser.getId())
                .reason(ReportReason.INNAPROPRIATE_BEHAVIOR)
                .details("Il fait caca partout")
                .build());
        Assertions.assertEquals(submitter.getId(), reportModel.getSubmitter().userId());
        Assertions.assertEquals(badUser.getId(), reportModel.getReportedUser().userId());
        Assertions.assertFalse(reportService.getBySubmitterId(submitter.getId()).isEmpty());
    }

    @Test
    void submitReportSelfTest() throws ParseException{
        String submitterEmail = "submitter@gmail.com";
        UserEntity submitter = anyUser(submitterEmail);
        Assertions.assertThrows(ForbiddenOperationException.class, () -> reportService.submitReport(submitterEmail, SubmitReportRequest.builder()
                .reportedUserId(submitter.getId())
                .reason(ReportReason.INNAPROPRIATE_BEHAVIOR)
                .details("Il fait caca partout")
                .build()));
    }

    @Test
    void updateReportTest() throws ParseException{
        String expectedDetails = "Il fait pipi partout";
        String submitterEmail = "submitter@gmail.com";
        anyUser(submitterEmail);
        UserEntity badUser = anyUser("user@gmail.com");
        long reportId = reportService.submitReport(submitterEmail, SubmitReportRequest.builder()
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
    void deleteReportTest() throws ParseException{
        String submitterEmail = "submitter@gmail.com";
        UserEntity submitter = anyUser(submitterEmail);
        UserEntity badUser = anyUser("user@gmail.com");
        long reportId = reportService.submitReport(submitterEmail, SubmitReportRequest.builder()
                .reportedUserId(badUser.getId())
                .reason(ReportReason.INNAPROPRIATE_BEHAVIOR)
                .details("Il fait caca partout")
                .build()).getId();
        reportService.deleteReportAdmin(reportId);
        Assertions.assertTrue(reportService.getBySubmitterId(submitter.getId()).isEmpty());
    }

    @Test
    void getByReportedUserTest() throws ParseException{
        int numberOfReports = 10;
        String submitterEmail = "submitter@gmail.com";
        anyUser(submitterEmail);
        UserEntity badUser = anyUser("user@gmail.com");
        for (int i = 0; i < numberOfReports; i++) {
            reportService.submitReport(submitterEmail, SubmitReportRequest.builder()
                    .reportedUserId(badUser.getId())
                    .reason(ReportReason.INNAPROPRIATE_BEHAVIOR)
                    .details("Il fait caca partout")
                    .build());
        }
        Assertions.assertEquals(numberOfReports, reportService.getByReportedUserId(badUser.getId()).size());
    }
}
