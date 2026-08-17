package com.abinash.campus_management.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private Long id;
    private Long userId;
    private String designation;
    private boolean hasEditAccess;
    private LocalDateTime joinedAt;
}
