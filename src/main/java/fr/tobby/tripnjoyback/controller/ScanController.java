package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.model.request.ScanRequest;
import fr.tobby.tripnjoyback.model.response.ScanResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("scan")
public class ScanController {

    @PostMapping
    public ScanResponse scan(@RequestBody ScanRequest request)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
