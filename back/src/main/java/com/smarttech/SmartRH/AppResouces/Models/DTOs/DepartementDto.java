package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDepartement;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartementDto {
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private TypeDepartement type;
    private Long chefId;
}
