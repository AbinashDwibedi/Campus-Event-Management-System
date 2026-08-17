package com.abinash.campus_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipRoleUpdateRequest {

    @NotBlank(message = "Designation cannot be blank")
    private String designation;

    @NotNull(message = "hasEditAccess flag cannot be null")
    private Boolean hasEditAccess;
}
