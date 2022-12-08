package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.service.CallService;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/call")
public class CallController {
    private static final Logger logger = LoggerFactory.getLogger(CallController.class);
    private final CallService callService;

    private final IdCheckerService idCheckerService;

    public CallController(CallService callService, final IdCheckerService idCheckerService) {
        this.callService = callService;
        this.idCheckerService = idCheckerService;
    }

    @GetMapping("rtc/{channelName}/{uid}")
    @Operation(summary = "Returns the token for the RTC call")
    @ApiResponse(responseCode = "200", description = "The token for the RTC call")
    public String getRtcToken(@PathVariable String channelName, @PathVariable int uid) {
        return callService.generateToken(channelName, uid);
    }

    @PostMapping("/start/{groupId}")
    public void startCall(@PathVariable("groupId") long groupId) {
        long currentUserId = idCheckerService.getCurrentUserId();
        String userName = idCheckerService.getCurrentUser().getFirstname();
        callService.sendGroupCallNotification(groupId, userName);
    }
}
