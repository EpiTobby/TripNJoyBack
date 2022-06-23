package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.model.request.ScanRequest;
import fr.tobby.tripnjoyback.model.response.ScanResponse;
import fr.tobby.tripnjoyback.service.ScanService;
import org.springframework.stereotype.Controller;
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
