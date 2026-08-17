package com.abinash.campus_management.services;

import com.abinash.campus_management.dto.*;
import com.abinash.campus_management.entity.ClubMemberships;
import com.abinash.campus_management.entity.Clubs;
import com.abinash.campus_management.entity.MyUser;
import com.abinash.campus_management.entity.Students;
import com.abinash.campus_management.enums.Category;
import com.abinash.campus_management.exception.ApiException;
import com.abinash.campus_management.repository.ClubMembershipRepository;
import com.abinash.campus_management.repository.ClubRepository;
import com.abinash.campus_management.repository.EventRepository;
import com.abinash.campus_management.repository.MyUserRepository;
import com.abinash.campus_management.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubMembershipRepository clubMembershipRepository;
    private final StudentRepository studentRepository;
    private final MyUserRepository userRepository;
    private final EventRepository eventRepository;
    private final ModelMapper mapper;

    @Transactional
    public ClubResponse createClub(ClubCreateRequest request) {
        if (clubRepository.existsByName(request.getName()))
            throw new ApiException(HttpStatus.CONFLICT, "A club with this name already exists");
        if (clubRepository.existsByClubCode(request.getClubCode()))
            throw new ApiException(HttpStatus.CONFLICT, "A club with this code already exists");

        Clubs club = mapper.map(request, Clubs.class);
        return mapper.map(clubRepository.save(club), ClubResponse.class);
    }

    @Transactional
    public List<ClubResponse> getActiveClubs() {
        return clubRepository.findByIsActiveTrue()
                .stream()
                .map(club -> mapper.map(club, ClubResponse.class))
                .toList();
    }

    @Transactional
    public ClubResponse getClubById(Long clubId) {
        Clubs club = findClubOrThrow(clubId);
        return mapper.map(club, ClubResponse.class);
    }

    @Transactional
    public MemberResponse joinClub(Long clubId, ClubJoinRequest request, String username) {
        Clubs club = findClubOrThrow(clubId);

        if (!club.isActive())
            throw new ApiException(HttpStatus.BAD_REQUEST, "This club is no longer active");

        MyUser user = userRepository.findByName(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        studentRepository.findByUser_Name(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Student profile not found. Please create your profile first."));

        if (clubMembershipRepository.existsByClubAndUser(club, user))
            throw new ApiException(HttpStatus.CONFLICT, "You are already a member of this club");

        ClubMemberships membership = ClubMemberships.builder()
                .club(club)
                .user(user)
                .designation(request.getDesignation())
                .build();

        return mapper.map(clubMembershipRepository.save(membership), MemberResponse.class);
    }

    @Transactional
    public List<ClubResponse> getMyClubs(String username) {
        MyUser user = userRepository.findByName(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        return clubMembershipRepository.findByUser(user)
                .stream()
                .map(membership -> mapper.map(membership.getClub(), ClubResponse.class))
                .toList();
    }

    @Transactional
    public List<MemberResponse> getMembers(Long clubId) {
        Clubs club = findClubOrThrow(clubId);
        return clubMembershipRepository.findByClub(club)
                .stream()
                .map(m -> mapper.map(m, MemberResponse.class))
                .toList();
    }

    @Transactional
    public void leaveClub(Long clubId, String username) {
        Clubs club = findClubOrThrow(clubId);
        MyUser user = userRepository.findByName(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        ClubMemberships membership = clubMembershipRepository.findByClubAndUser(club, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "You are not a member of this club"));
        clubMembershipRepository.delete(membership);
    }

    @Transactional
    public MemberResponse updateAccess(Long clubId, Long userId, boolean hasEditAccess, String requesterName) {
        Clubs club = findClubOrThrow(clubId);
        requireEditAccess(club, requesterName);
        ClubMemberships target = clubMembershipRepository.findByClubAndUser_Id(club, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Member not found in this club"));
        target.setHasEditAccess(hasEditAccess);
        return mapper.map(clubMembershipRepository.save(target), MemberResponse.class);
    }

    @Transactional
    public MemberResponse updateDesignation(Long clubId, Long userId, String designation, String requesterName) {
        Clubs club = findClubOrThrow(clubId);
        requireEditAccess(club, requesterName);
        ClubMemberships target = clubMembershipRepository.findByClubAndUser_Id(club, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Member not found in this club"));
        target.setDesignation(designation);
        return mapper.map(clubMembershipRepository.save(target), MemberResponse.class);
    }

    @Transactional
    public void removeMember(Long clubId, Long userId, String requesterName) {
        Clubs club = findClubOrThrow(clubId);
        requireEditAccess(club, requesterName);
        ClubMemberships target = clubMembershipRepository.findByClubAndUser_Id(club, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Member not found in this club"));
        clubMembershipRepository.delete(target);
    }

    private void requireEditAccess(Clubs club, String requesterName) {
        MyUser requester = userRepository.findByName(requesterName)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = requester.getAuthorities().name().equals("ROLE_ADMIN");
        if (isAdmin)
            return;

        ClubMemberships membership = clubMembershipRepository.findByClubAndUser(club, requester)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "You are not a member of this club"));

        if (!membership.isHasEditAccess())
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have edit access for this club");
    }

    private Clubs findClubOrThrow(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Club not found with id: " + clubId));
    }

    @Transactional
    public ClubResponse updateClub(Long clubId, ClubUpdateRequest request, String requesterName) {
        Clubs club = findClubOrThrow(clubId);
        requireEditAccess(club, requesterName);
        club.setName(request.getName());
        club.setCategory(request.getCategory());
        club.setContactEmail(request.getContactEmail());
        return mapper.map(clubRepository.save(club), ClubResponse.class);
    }

    @Transactional
    public ClubResponse toggleStatus(Long clubId, boolean isActive) {
        Clubs club = findClubOrThrow(clubId);
        club.setActive(isActive);
        return mapper.map(clubRepository.save(club), ClubResponse.class);
    }

    @Transactional
    public List<ClubResponse> getByCategory(Category category) {
        return clubRepository.findByIsActiveTrueAndCategory(category)
                .stream()
                .map(club -> mapper.map(club, ClubResponse.class))
                .toList();
    }

    @Transactional
    public List<EventResponse> getClubEvents(Long clubId) {
        findClubOrThrow(clubId);
        return eventRepository.findByClub_Id(clubId)
                .stream()
                .map(event -> {
                    EventResponse r = mapper.map(event, EventResponse.class);
                    r.setRegistrationCount(event.getRegistrations().size());
                    r.setClubId(event.getClub().getId());
                    r.setClubName(event.getClub().getName());
                    return r;
                })
                .toList();
    }
}
