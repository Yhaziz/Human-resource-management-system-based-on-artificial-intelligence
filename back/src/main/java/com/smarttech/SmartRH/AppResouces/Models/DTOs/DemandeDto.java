package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import com.smarttech.SmartRH.AppResouces.Models.ENUMs.CategorieConge;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.StatusDemande;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDemande;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DemandeDto {

    private Long id;

    private Long userId;

    private TypeDemande type;
    @Enumerated(EnumType.STRING)
    private CategorieConge categorie;
    private String motif;
    private LocalDateTime debut;
    private LocalDateTime fin;

    private List<String> attachments;
    @Enumerated(EnumType.STRING)
    private StatusDemande statusCd;
    @Enumerated(EnumType.STRING)
    private StatusDemande statusRrh;
    @Enumerated(EnumType.STRING)
    private StatusDemande statusDg;

    private LocalDateTime dateCreation;
    private LocalDateTime dateReponse;
    private String noteResponse;
    private boolean canceled;
}
