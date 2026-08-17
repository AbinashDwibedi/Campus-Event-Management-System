package com.abinash.campus_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubJoinRequest {

    @NotBlank(message = "Designation cannot be blank")
    private String designation;
}
