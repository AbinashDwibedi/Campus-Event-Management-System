package com.abinash.campus_management.controller;

import com.abinash.campus_management.dto.*;
import com.abinash.campus_management.services.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<SuccessResponse<ClubResponse>> createClub(
            @Valid @RequestBody ClubCreateRequest request) {
        ClubResponse response = clubService.createClub(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse<>(HttpStatus.CREATED, "Club created successfully", response));
    }

    @GetMapping("")
    public ResponseEntity<SuccessResponse<List<ClubResponse>>> getActiveClubs() {
        List<ClubResponse> clubs = clubService.getActiveClubs();
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Active clubs retrieved", clubs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ClubResponse>> getClubById(@PathVariable Long id) {
        ClubResponse response = clubService.getClubById(id);
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Club retrieved successfully", response));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<SuccessResponse<MemberResponse>> joinClub(
            @PathVariable Long id,
            @Valid @RequestBody ClubJoinRequest request,
            Authentication authentication) {
        MemberResponse response = clubService.joinClub(id, request, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse<>(HttpStatus.CREATED, "Successfully joined the club", response));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<SuccessResponse<List<MemberResponse>>> getMembers(@PathVariable Long id) {
        List<MemberResponse> members = clubService.getMembers(id);
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Members retrieved successfully", members));
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<SuccessResponse<Void>> leaveClub(
            @PathVariable Long id, Authentication authentication) {
        clubService.leaveClub(id, authentication.getName());
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "You have left the club"));
    }

    @PatchMapping("/{id}/members/{userId}/access")
    public ResponseEntity<SuccessResponse<MemberResponse>> updateAccess(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody AccessUpdateRequest request,
            Authentication authentication) {
        MemberResponse response = clubService.updateAccess(id, userId, request.getHasEditAccess(),
                authentication.getName());
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Access updated successfully", response));
    }

    @PatchMapping("/{id}/members/{userId}/designation")
    public ResponseEntity<SuccessResponse<MemberResponse>> updateDesignation(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody ClubJoinRequest request,
            Authentication authentication) {
        MemberResponse response = clubService.updateDesignation(id, userId, request.getDesignation(),
                authentication.getName());
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Designation updated successfully", response));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<SuccessResponse<Void>> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            Authentication authentication) {
        clubService.removeMember(id, userId, authentication.getName());
        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK, "Member removed successfully"));
    }
}
