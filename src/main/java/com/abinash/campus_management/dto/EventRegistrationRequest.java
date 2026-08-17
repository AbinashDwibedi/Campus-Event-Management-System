package com.abinash.campus_management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationRequest {

    @NotNull(message = "Event ID cannot be null")
    @Positive(message = "Event ID must be a positive number")
    private Long eventId;
}
