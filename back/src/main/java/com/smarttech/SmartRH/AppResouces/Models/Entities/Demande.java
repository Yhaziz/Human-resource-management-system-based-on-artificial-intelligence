package com.smarttech.SmartRH.AppResouces.Models.Entities;

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
@Entity
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private TypeDemande type;
    @Enumerated(EnumType.STRING)
    private CategorieConge categorie;
    private String motif;
    private LocalDateTime debut;
    private LocalDateTime fin;
    @OneToMany(mappedBy = "demande", cascade = CascadeType.REMOVE)
    private List<Attachment> attachments;
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
