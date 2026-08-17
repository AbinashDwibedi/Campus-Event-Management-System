package com.abinash.campus_management.controller;

import com.abinash.campus_management.dto.*;
import com.abinash.campus_management.services.EventServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventServices eventServices;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<SuccessResponse<EventResponse>> createEvent(
            @RequestParam Long clubId,
            @Valid @RequestBody EventCreateRequest request) {
        EventResponse response = eventServices.createEvent(clubId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse<>(HttpStatus.CREATED, "Event created successfully", response));
    }

    @GetMapping("")
    public ResponseEntity<SuccessResponse<Page<EventResponse>>> getUpcomingEvents(Pageable pageable) {
        Page<EventResponse> events = eventServices.getUpcomingEvents(pageable);
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Upcoming events retrieved", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<EventResponse>> getEventById(@PathVariable Long id) {
        EventResponse response = eventServices.getEventById(id);
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Event retrieved successfully", response));
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<SuccessResponse<RegistrationResponse>> registerForEvent(
            @PathVariable Long id,
            Authentication authentication) {
        RegistrationResponse response = eventServices.registerForEvent(id, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse<>(HttpStatus.CREATED, "Successfully registered for the event", response));
    }

    @DeleteMapping("/{id}/register")
    public ResponseEntity<SuccessResponse<Void>> cancelRegistration(
            @PathVariable Long id,
            Authentication authentication) {
        eventServices.cancelRegistration(id, authentication.getName());
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Registration cancelled successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/students")
    public ResponseEntity<SuccessResponse<List<RegistrationResponse>>> getAttendeesForEvent(
            @PathVariable Long id) {
        List<RegistrationResponse> attendees = eventServices.getAttendeesForEvent(id);
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Attendees retrieved successfully", attendees));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<SuccessResponse<EventResponse>> updateEventStatus(
            @PathVariable Long id,
            @Valid @RequestBody EventStatusUpdateRequest request) {
        EventResponse response = eventServices.updateEventStatus(id, request);
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Event status updated", response));
    }
}
