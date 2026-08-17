package com.abinash.campus_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipAddRequest {

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @NotBlank(message = "Designation cannot be blank")
    private String designation;
}
