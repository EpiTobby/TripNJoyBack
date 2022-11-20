package fr.tripnjoy.profiles.api.client;

import fr.tripnjoy.profiles.dto.request.SubmitReportRequest;
import fr.tripnjoy.profiles.dto.request.UpdateReportRequest;
import fr.tripnjoy.profiles.dto.response.ReportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "SERVICE-REPORTS", contextId = "SERVICE-REPORTS-REPORT", path = "reports")
public interface ReportFeignClient {

    @PostMapping("")
    ReportResponse submitReport(@RequestHeader("userId") long userId, @RequestBody SubmitReportRequest submitReportRequest);

    @GetMapping("{id}")
    List<ReportResponse> getBySubmitterId(@PathVariable("id") long submitterId);

    @GetMapping("admin/{id}")
    List<ReportResponse> getByReportedUserId(@RequestHeader("roles") List<String> roles, @PathVariable("id") long reportedUserId);

    @PatchMapping("{id}")
    ReportResponse updateReport(@RequestHeader("userId") long userId, @PathVariable("id") long reportId, @RequestBody UpdateReportRequest updateReportRequest);

    @DeleteMapping("{id}/")
    void deleteReport(@RequestHeader("userId") long userId, @PathVariable("id") long reportId);

    @DeleteMapping("{id}/admin")
    void deleteReportAdmin(@RequestHeader("roles") List<String> roles, @PathVariable("id") long reportId);
}
