package com.abinash.campus_management.dto;

import com.abinash.campus_management.enums.Category;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubCreateRequest {

    @NotBlank(message = "Club name cannot be blank")
    @Size(max = 100, message = "Club name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Club code cannot be blank")
    @Size(min = 2, max = 6, message = "Club code must be between 2 and 6 characters")
    private String clubCode;

    @NotNull(message = "Category cannot be null")
    private Category category;

    @NotBlank(message = "Contact email cannot be blank")
    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;
}
