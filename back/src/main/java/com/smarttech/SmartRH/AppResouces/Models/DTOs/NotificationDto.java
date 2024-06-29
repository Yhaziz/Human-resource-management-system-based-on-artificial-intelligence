package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private String origineNom;
    private String origineImgPath;
    private String text;
    private boolean vue;
    private LocalDateTime dateCreation;
}
