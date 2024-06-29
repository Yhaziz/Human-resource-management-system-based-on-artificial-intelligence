package com.smarttech.SmartRH.AppResouces.Services.interfaces;

import com.smarttech.SmartRH.AppResouces.Exceptions.DemandeException;
import com.smarttech.SmartRH.AppResouces.Exceptions.PermissionException;

import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.DemandeDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DemandeService {
    public DemandeDto addDemande(DemandeDto dto, List<MultipartFile> files) throws DemandeException, IOException, UserException;

    DemandeDto deleteDemande(Long demId) throws DemandeException, PermissionException, IOException;

    DemandeDto cancelDemande(Long demId) throws DemandeException, PermissionException, UserException;

    public List<DemandeDto> getCurrentUserDemandes() throws DemandeException;

    List<DemandeDto> getTeamDemandes();


    public DemandeDto acceptDemande(Long demId) throws DemandeException, PermissionException, UserException;
    public DemandeDto rejectDemande(Long demId, String note) throws DemandeException, PermissionException, UserException;

}
