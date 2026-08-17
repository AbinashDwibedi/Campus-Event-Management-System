package com.abinash.campus_management.dto;

import com.abinash.campus_management.enums.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubResponse {

    private Long id;
    private String clubCode;
    private String name;
    private Category category;
    private String contactEmail;
    private boolean isActive;
}
