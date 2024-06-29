package com.smarttech.SmartRH.AppResouces.Services.interfaces;

import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.NotificationDto;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.StatusDemande;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDemande;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Demande;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;

import java.util.List;

public interface NotificationService {
    NotificationDto addNotification(User origine, Demande demande) throws UserException;
    List<NotificationDto> getNotifications() throws UserException;

    List<NotificationDto> seeAllNotifications() throws UserException;


    List<NotificationDto> clearNotifications() throws UserException;

    NotificationDto cancelDemandeNotification(User origine, Demande demande) throws UserException;
}
