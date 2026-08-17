package com.abinash.campus_management.services;

import com.abinash.campus_management.dto.*;
import com.abinash.campus_management.entity.EventRegistrations;
import com.abinash.campus_management.entity.Events;
import com.abinash.campus_management.entity.Students;
import com.abinash.campus_management.enums.Status;
import com.abinash.campus_management.exception.ApiException;
import com.abinash.campus_management.repository.ClubRepository;
import com.abinash.campus_management.repository.EventRegistrationRepository;
import com.abinash.campus_management.repository.EventRepository;
import com.abinash.campus_management.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServices {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final StudentRepository studentRepository;
    private final ClubRepository clubRepository;

    @Transactional
    public EventResponse createEvent(Long clubId, EventCreateRequest request) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Club not found with id: " + clubId));

        Events event = Events.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .venue(request.getVenue())
                .maxCapacity(request.getMaxCapacity())
                .status(Status.UPCOMING)
                .club(club)
                .build();

        Events saved = eventRepository.save(event);
        return toEventResponse(saved);
    }

    @Transactional
    public Page<EventResponse> getUpcomingEvents(Pageable pageable) {
        return eventRepository
                .findByStatusOrderByStartTimeAsc(Status.UPCOMING, pageable)
                .map(this::toEventResponse);
    }

    @Transactional
    public EventResponse getEventById(Long eventId) {
        Events event = findEventOrThrow(eventId);
        return toEventResponse(event);
    }

    @Transactional
    public RegistrationResponse registerForEvent(Long eventId, String username) {
        Events event = findEventOrThrow(eventId);

        if (event.getStatus() != Status.UPCOMING) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Registration is only allowed for UPCOMING events");
        }

        Students student = studentRepository.findByUser_Name(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Student profile not found. Please create your profile first."));

        if (eventRegistrationRepository.existsByEventAndStudent(event, student)) {
            throw new ApiException(HttpStatus.CONFLICT, "You are already registered for this event");
        }

        int currentCount = eventRepository.countRegistrationsByEventId(eventId);
        if (currentCount >= event.getMaxCapacity()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Event has reached its maximum capacity");
        }

        EventRegistrations registration = EventRegistrations.builder()
                .event(event)
                .student(student)
                .build();

        EventRegistrations saved = eventRegistrationRepository.save(registration);
        return toRegistrationResponse(saved);
    }

    @Transactional
    public void cancelRegistration(Long eventId, String username) {
        Events event = findEventOrThrow(eventId);

        Students student = studentRepository.findByUser_Name(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student profile not found"));

        EventRegistrations registration = eventRegistrationRepository
                .findByEventAndStudent(event, student)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "No registration found for this event"));

        eventRegistrationRepository.delete(registration);
    }

    @Transactional
    public List<RegistrationResponse> getAttendeesForEvent(Long eventId) {
        Events event = findEventOrThrow(eventId);
        return eventRegistrationRepository.findByEvent(event)
                .stream()
                .map(this::toRegistrationResponse)
                .toList();
    }

    @Transactional
    public EventResponse updateEventStatus(Long eventId, EventStatusUpdateRequest request) {
        Events event = findEventOrThrow(eventId);
        event.setStatus(request.getStatus());
        return toEventResponse(eventRepository.save(event));
    }

    private Events findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Event not found with id: " + eventId));
    }

    private EventResponse toEventResponse(Events event) {
        return EventResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .venue(event.getVenue())
                .maxCapacity(event.getMaxCapacity())
                .registrationCount(event.getRegistrations().size())
                .status(event.getStatus())
                .clubId(event.getClub().getId())
                .clubName(event.getClub().getName())
                .build();
    }

    private RegistrationResponse toRegistrationResponse(EventRegistrations reg) {
        Events event = reg.getEvent();
        Students student = reg.getStudent();
        return RegistrationResponse.builder()
                .registrationId(reg.getId())
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventVenue(event.getVenue())
                .eventStartTime(event.getStartTime())
                .studentId(student.getId())
                .studentName(student.getName())
                .registeredAt(reg.getRegisteredAt())
                .build();
    }
}
