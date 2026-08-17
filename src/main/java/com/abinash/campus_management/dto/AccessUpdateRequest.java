package com.abinash.campus_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessUpdateRequest {

    @NotNull(message = "hasEditAccess cannot be null")
    private Boolean hasEditAccess;
}
