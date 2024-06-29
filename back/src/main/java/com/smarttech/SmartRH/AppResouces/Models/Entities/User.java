package com.smarttech.SmartRH.AppResouces.Models.Entities;

import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Sexe;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String cin;
    private String imgPath;
    private String nomComplet;
    private String qualification;
    @Enumerated(EnumType.STRING)
    private Sexe sexe;
    @Column(unique = true)
    private String telephone;
    private LocalDate dob;
    private LocalDate doj;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "departement_id")
    private Departement departement;
    private boolean independant;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isEnabled;
    private LocalDateTime dateCreation;

}
