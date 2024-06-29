package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer ";
    @Enumerated(EnumType.STRING)
    private Role loggedRole;

    public AuthResponseDTO(String accessToken, Role loggedRole) {
        this.accessToken = accessToken;
        this.loggedRole = loggedRole;
    }
}