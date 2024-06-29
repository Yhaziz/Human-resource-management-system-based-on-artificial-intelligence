package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDto {
    private String currentPassword;

    private String newPassword;

    private String confirmPassword;



}