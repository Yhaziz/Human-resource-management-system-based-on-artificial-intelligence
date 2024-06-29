package com.smarttech.SmartRH.AppResouces.Models.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origine_user_id")
    private User origine;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "notification_destination",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> destination;

    private String text;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "notification_vue_par",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> vuePar;

    private LocalDateTime dateCreation;

}