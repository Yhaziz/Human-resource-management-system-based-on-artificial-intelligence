package com.smarttech.SmartRH.AppResouces.Models.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;
    private LocalDateTime dateUpload;
    @ManyToOne
    @JoinColumn(name = "demande_id")
    private Demande demande;
}
