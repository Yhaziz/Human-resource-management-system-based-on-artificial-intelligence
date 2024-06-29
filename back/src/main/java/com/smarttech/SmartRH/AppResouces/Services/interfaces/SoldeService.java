package com.smarttech.SmartRH.AppResouces.Services.interfaces;

import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.SoldeDto;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Solde;

import java.util.List;

public interface SoldeService {
    SoldeDto addSolde(Long userId) throws UserException;
    SoldeDto updateSolde(Long userId, SoldeDto dto) throws UserException;
    SoldeDto deleteSolde(Long userId) throws UserException;
    SoldeDto getSolde(Long userId) throws UserException;
    List<SoldeDto> getAllSolde();


    SoldeDto subtractFromSoldeConge(Long userId, int prisCongeJours) throws UserException;


    SoldeDto subtractFromSoldeSortie(Long userId, int prisSortieMin) throws UserException;

    SoldeDto addToSoldeConge(Long userId, int prisCongeJours) throws UserException;

    SoldeDto addToSoldeSortie(Long userId, int prisSortieMin) throws UserException;

}
