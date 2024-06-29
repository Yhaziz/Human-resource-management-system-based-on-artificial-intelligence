package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Sexe;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String cin;
    private String imgPath;
    private String nomComplet;
    private String qualification;
    @Enumerated(EnumType.STRING)
    private Sexe sexe;
    private String telephone;
    private LocalDate dob;
    private LocalDate doj;
    private Long departementId;
    private String departementName;
    private boolean independant;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isEnabled;
    private LocalDateTime dateCreation;
}
