package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    private String username;
    private String password;
    private Role role;
}