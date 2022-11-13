package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.service.CallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/call")
public class CallController {
    private static final Logger logger = LoggerFactory.getLogger(CallController.class);
    private final CallService callService;
    public CallController(CallService callService) {
        this.callService = callService;
    }

    @GetMapping("rtc/{channelName}/{uid}")
    @Operation(summary = "Returns the token for the RTC call")
    @ApiResponse(responseCode = "200", description = "The token for the RTC call")
    public String getRtcToken(@PathVariable String channelName, @PathVariable int uid) {
        return callService.generateToken(channelName, uid);
    }
}
