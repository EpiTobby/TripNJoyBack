package fr.tripnjoy.expenses.controller;

import fr.tripnjoy.expenses.dto.request.ScanRequest;
import fr.tripnjoy.expenses.dto.response.ScanResponse;
import fr.tripnjoy.expenses.service.ScanService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/scan")
public class ScanController {

    private final ScanService scanService;

    public ScanController(final ScanService scanService)
    {
        this.scanService = scanService;
    }

    @PostMapping
    public ScanResponse scan(@RequestBody ScanRequest request)
    {
        return scanService.scan(request.getMinioUrl());
    }
}
