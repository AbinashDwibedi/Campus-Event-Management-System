package com.abinash.campus_management.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCreateRequest {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Start time cannot be null")
    @Future(message = "Start time must be a future date and time")
    private LocalDateTime startTime;

    @NotBlank(message = "Venue cannot be blank")
    @Size(max = 255, message = "Venue must not exceed 255 characters")
    private String venue;

    @NotNull(message = "Max capacity cannot be null")
    @Min(value = 1, message = "Max capacity must be at least 1")
    private Integer maxCapacity;
}
