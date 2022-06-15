package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.ReportNotFoundException;
import fr.tobby.tripnjoyback.model.ReportModel;
import fr.tobby.tripnjoyback.model.request.SubmitReportRequest;
import fr.tobby.tripnjoyback.model.request.UpdateReportRequest;
import fr.tobby.tripnjoyback.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("")
    @Operation(summary = "Create a report")
    @ApiResponse(responseCode = "200", description = "The report has been created")
    @ApiResponse(responseCode = "422", description = "The submitter or reported user do not exist")
    public ReportModel submitReport(@RequestBody SubmitReportRequest submitReportRequest) {
        String submitterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return reportService.submitReport(submitterEmail, submitReportRequest);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get all the report posted by a user")
    public List<ReportModel> getBySubmitterId(@PathVariable("id") long submitterId) {
        return reportService.getBySubmitterId(submitterId);
    }

    @GetMapping("admin/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Get all the report of a user")
    public List<ReportModel> getByReporterUserId(@PathVariable("id") long reportedUserId) {
        return reportService.getByReportedUserId(reportedUserId);
    }

    @PatchMapping("{id}")
    @Operation(summary = "Update a report")
    @ApiResponse(responseCode = "200", description = "The report has been updated")
    @ApiResponse(responseCode = "404", description = "The report does not exist")
    public ReportModel updateReport(@PathVariable("id") long reportId, @RequestBody UpdateReportRequest updateReportRequest) {
        return reportService.updateReport(reportId, updateReportRequest);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a report")
    @ApiResponse(responseCode = "200", description = "The report has been deleted")
    @ApiResponse(responseCode = "404", description = "The report does not exist")
    public void deleteReport(@PathVariable("id") long reportId) {
        reportService.deleteReport(reportId);
    }

    @ExceptionHandler(ReportNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String getError(ReportNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
