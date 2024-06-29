package com.smarttech.SmartRH.AppResouces.Models.Entities;

import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDepartement;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Departement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private TypeDepartement type;
    @OneToOne
    @JoinColumn(name = "chef_id")
    private User chefDepartement;
    @OneToMany(mappedBy = "departement", cascade = CascadeType.ALL)
    private List<User> users;
}
