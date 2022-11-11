package fr.tripnjoy.reports.service;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.profiles.model.ReportReason;
import fr.tripnjoy.reports.model.ReportModel;
import fr.tripnjoy.reports.repository.ReportRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
class ReportServiceTest {
    @Autowired
    private ReportRepository reportRepository;

    private ReportService reportService;
    private UserFeignClient userFeignClient;

    @BeforeEach
    void setUp()
    {
        userFeignClient = mock(UserFeignClient.class);
        when(userFeignClient.exists(anyLong())).thenReturn(new BooleanResponse(false));
        reportService = new ReportService(reportRepository, userFeignClient);
    }

    @AfterEach
    void tearDown()
    {
        reportRepository.deleteAll();
    }

    private long userIdCounter = 1;

    private long anyUser()
    {
        when(userFeignClient.exists(userIdCounter)).thenReturn(new BooleanResponse(true));
        return userIdCounter++;
    }

    @Test
    void submitReportTest()
    {
        long submitter = anyUser();
        long badUser = anyUser();
        ReportModel reportModel = reportService.submitReport(submitter, badUser, ReportReason.INNAPROPRIATE_BEHAVIOR, "pas cool");
        Assertions.assertEquals(submitter, reportModel.getSubmitter());
        Assertions.assertEquals(badUser, reportModel.getReportedUser());
        Assertions.assertFalse(reportService.getBySubmitterId(submitter).isEmpty());
    }

    @Test
    void submitReportSelfTest()
    {
        long submitter = anyUser();
        Assertions.assertThrows(ForbiddenOperationException.class, () -> reportService.submitReport(submitter, submitter, ReportReason.INNAPROPRIATE_BEHAVIOR, "pas cool"));
    }

    @Test
    void updateReportTest()
    {
        String expectedDetails = "Il fait pipi partout";
        long submitter = anyUser();
        long badUser = anyUser();
        long reportId = reportService.submitReport(submitter, badUser, ReportReason.INNAPROPRIATE_BEHAVIOR, "pas cool")
                                     .getId();
        ReportModel reportModel = reportService.updateReport(submitter, reportId, ReportReason.HYGIENE_PROBLEM, expectedDetails);
        Assertions.assertEquals(ReportReason.HYGIENE_PROBLEM, reportModel.getReason());
        Assertions.assertEquals(expectedDetails, reportModel.getDetails());
    }

    @Test
    void deleteReportTest()
    {
        long submitter = anyUser();
        long badUser = anyUser();
        long reportId = reportService.submitReport(submitter, badUser, ReportReason.INNAPROPRIATE_BEHAVIOR, "pas cool").getId();
        reportService.deleteReportAdmin(reportId);
        Assertions.assertTrue(reportService.getBySubmitterId(submitter).isEmpty());
    }

    @Test
    void getByReportedUserTest()
    {
        int numberOfReports = 10;
        long submitter = anyUser();
        long badUser = anyUser();
        for (int i = 0; i < numberOfReports; i++)
            reportService.submitReport(submitter, badUser, ReportReason.INNAPROPRIATE_BEHAVIOR, "pas cool");
        Assertions.assertEquals(numberOfReports, reportService.getByReportedUserId(badUser).size());
    }
}
