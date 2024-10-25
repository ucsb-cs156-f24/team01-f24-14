package edu.ucsb.cs156.example.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.example.entities.HelpRequests;
import edu.ucsb.cs156.example.repositories.HelpRequestsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a REST controller for managing Help Requests
 */
@RestController
@Slf4j
@RequestMapping("/api/HelpRequests")
@Tag(name = "HelpRequests")
public class HelpRequestsController extends ApiController {

    @Autowired
    private HelpRequestsRepository helpRequestsRepository;

    /**
     * List all help requests
     * 
     * @return an iterable of help requests
     */
    @Operation(summary= "List all help requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<HelpRequests> allHelpRequests() {
        return helpRequestsRepository.findAll();
    }

    /**
     * Create a new help request
     * 
     * @param requesterEmail the email of the requester
     * @param teamId the ID of the team
     * @param tableOrBreakoutRoom the table or breakout room location
     * @param explanation explanation of the help request
     * @param solved status of the help request, whether it's solved or not
     * @param requestTime the time the request was made
     * @return the newly created help request
     */
    @Operation(summary= "Create a new help request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public HelpRequests postHelpRequest(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="teamId") @RequestParam String teamId,
            @Parameter(name="tableOrBreakoutRoom") @RequestParam String tableOrBreakoutRoom,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="solved") @RequestParam Boolean solved,
            @Parameter(name="requestTime", description="ISO date format, e.g., YYYY-mm-ddTHH:MM:SS") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestTime) {

        log.info("Creating new help request: {}, at {}", requesterEmail, requestTime);

        HelpRequests helpRequest = new HelpRequests();
        helpRequest.setRequesterEmail(requesterEmail);
        helpRequest.setTeamId(teamId);
        helpRequest.setTableOrBreakoutRoom(tableOrBreakoutRoom);
        helpRequest.setExplanation(explanation);
        helpRequest.setSolved(solved);
        helpRequest.setRequestTime(requestTime);

        return helpRequestsRepository.save(helpRequest);
    }
}
