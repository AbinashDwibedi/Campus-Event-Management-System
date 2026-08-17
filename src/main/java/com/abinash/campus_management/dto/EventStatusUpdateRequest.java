package com.abinash.campus_management.dto;

import com.abinash.campus_management.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventStatusUpdateRequest {

    @NotNull(message = "Status cannot be null")
    private Status status;
}
