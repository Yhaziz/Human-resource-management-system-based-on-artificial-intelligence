package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SoldeDto {
    private Long id;

    private Long userId;
    private int totalCongeJours;
    private int prisCongeJours;
    private int totalSortieMin;
    private int prisSortieMin;
    private int compteurSortie;
}
