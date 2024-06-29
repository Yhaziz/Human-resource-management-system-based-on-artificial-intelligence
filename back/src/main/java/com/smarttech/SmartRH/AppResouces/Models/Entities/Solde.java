package com.smarttech.SmartRH.AppResouces.Models.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Solde {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;
    private int totalCongeJours;
    private int prisCongeJours;
    private int totalSortieMin;
    private int prisSortieMin;
    private int compteurSortie;

}
