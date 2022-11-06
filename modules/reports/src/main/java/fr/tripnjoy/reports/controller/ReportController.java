package fr.tripnjoy.reports.controller;

import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.common.exception.UnauthorizedException;
import fr.tripnjoy.profiles.dto.request.SubmitReportRequest;
import fr.tripnjoy.profiles.dto.request.UpdateReportRequest;
import fr.tripnjoy.profiles.dto.response.ReportResponse;
import fr.tripnjoy.profiles.exception.ReportNotFoundException;
import fr.tripnjoy.reports.model.ReportModel;
import fr.tripnjoy.reports.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportService reportService;

    public ReportController(ReportService reportService)
    {
        this.reportService = reportService;
    }

    @PostMapping("")
    @Operation(summary = "Create a report")
    @ApiResponse(responseCode = "200", description = "The report has been created")
    @ApiResponse(responseCode = "422", description = "The submitter or reported user do not exist")
    public ReportResponse submitReport(@RequestHeader("userId") long userId, @RequestBody SubmitReportRequest submitReportRequest)
    {
        return reportService.submitReport(userId,
                                    submitReportRequest.getReportedUserId(),
                                    submitReportRequest.getReason(),
                                    submitReportRequest.getDetails())
                            .toDtoResponse();
    }

    @GetMapping("{id}")
    @Operation(summary = "Get all the report posted by a user")
    public List<ReportResponse> getBySubmitterId(@PathVariable("id") long submitterId)
    {
        return reportService.getBySubmitterId(submitterId).stream().map(ReportModel::toDtoResponse).toList();
    }

    @GetMapping("admin/{id}")
    @Operation(summary = "Get all the report of a user")
    public List<ReportResponse> getByReportedUserId(@RequestHeader("userRoles") List<String> roles, @PathVariable("id") long reportedUserId)
    {
        if (!roles.contains("admin"))
            throw new UnauthorizedException();
        return reportService.getByReportedUserId(reportedUserId).stream().map(ReportModel::toDtoResponse).toList();
    }

    @PatchMapping("{id}")
    @Operation(summary = "Update a report")
    @ApiResponse(responseCode = "200", description = "The report has been updated")
    @ApiResponse(responseCode = "404", description = "The report does not exist")
    public ReportResponse updateReport(@RequestHeader("userId") long userId, @PathVariable("id") long reportId, @RequestBody UpdateReportRequest updateReportRequest)
    {
        return reportService.updateReport(userId, reportId, updateReportRequest.getReason(), updateReportRequest.getDetails())
                            .toDtoResponse();
    }

    @DeleteMapping("{id}/")
    @Operation(summary = "Delete a report")
    @ApiResponse(responseCode = "200", description = "The report has been deleted")
    @ApiResponse(responseCode = "404", description = "The report does not exist")
    public void deleteReport(@RequestHeader("userId") long userId, @PathVariable("id") long reportId)
    {
        reportService.deleteReport(userId, reportId);
    }

    @DeleteMapping("{id}/admin")
    @Operation(summary = "Delete a report")
    @ApiResponse(responseCode = "200", description = "The report has been deleted")
    @ApiResponse(responseCode = "404", description = "The report does not exist")
    public void deleteReportAdmin(@RequestHeader("userRoles") List<String> roles, @PathVariable("id") long reportId)
    {
        if (!roles.contains("admin"))
            throw new UnauthorizedException();
        reportService.deleteReportAdmin(reportId);
    }

    @ExceptionHandler(ReportNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String getError(ReportNotFoundException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
