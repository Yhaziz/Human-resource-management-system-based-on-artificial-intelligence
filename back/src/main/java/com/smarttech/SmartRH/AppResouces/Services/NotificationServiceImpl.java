package com.smarttech.SmartRH.AppResouces.Services;

import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.NotificationDto;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.StatusDemande;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDemande;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDepartement;
import com.smarttech.SmartRH.AppResouces.Models.Entities.*;
import com.smarttech.SmartRH.AppResouces.Repository.DepartementRepository;
import com.smarttech.SmartRH.AppResouces.Repository.NotificationRepository;
import com.smarttech.SmartRH.AppResouces.Services.interfaces.NotificationService;
import com.smarttech.SmartRH.AppUtils.Sms.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    DepartementRepository departementRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    private SmsUtil smsUtil;


    @Override
    public NotificationDto addNotification(User origine, Demande demande) throws UserException {
        List<Departement> listRH = departementRepository.findAll()
                .stream().filter((dept) -> dept.getType() == TypeDepartement.RH)
                .collect(Collectors.toList());
        List<Departement> listD = departementRepository.findAll()
                .stream().filter((dept) -> dept.getType() == TypeDepartement.DIRECTION)
                .collect(Collectors.toList());

        User dg = listD.get(0).getChefDepartement();
        User rrh = listRH.get(0).getChefDepartement();
        User cd = demande.getUser().getDepartement().getChefDepartement();
        Notification notification = new Notification();

        List<User> destination = new ArrayList<User>();
        String text = "";

        boolean sms = false;

        Role demandeMaker = demande.getUser().getRole();
        StatusDemande statusCd = demande.getStatusCd();
        StatusDemande statusRrh = demande.getStatusRrh();
        StatusDemande statusDg = demande.getStatusDg();
        TypeDemande typeDemande = demande.getType();

        if (demandeMaker == Role.DG){
            if (statusRrh == StatusDemande.PENDING){
                notification.setOrigine(dg);
                destination.add(rrh);
                if (typeDemande == TypeDemande.CONGE){
                    text = dg.getNomComplet() + " a soumis une demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = dg.getNomComplet() + " demande une autorisation de sortie.";
                }else {
                    text = dg.getNomComplet() + " a déposé une demande explicative du retard.";
                }

            } else if (statusRrh == StatusDemande.ACCEPTED) {
                notification.setOrigine(rrh);
                destination.add(dg);
                if (typeDemande == TypeDemande.CONGE){
                    text = rrh.getNomComplet() + " a accepté votre demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = rrh.getNomComplet() + " a accepté votre demande d'autorisation de sortie.";
                }else {
                    text = rrh.getNomComplet() + " a accepté votre demande explicative du retard.";
                }
                sms = true;

            }else {
                notification.setOrigine(rrh);
                destination.add(dg);
                if (typeDemande == TypeDemande.CONGE){
                    text = rrh.getNomComplet() + " a refusé votre demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = rrh.getNomComplet() + " a refusé votre demande d'autorisation de sortie.";
                }else {
                    text = rrh.getNomComplet() + " a refusé votre demande explicative du retard.";
                }
                sms = true;
            }
        } else if (demandeMaker == Role.RRH) {
            if (statusDg == StatusDemande.PENDING){
                notification.setOrigine(rrh);
                destination.add(dg);
                if (typeDemande == TypeDemande.CONGE){
                    text = rrh.getNomComplet() + " a soumis une demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = rrh.getNomComplet() + " demande une autorisation de sortie.";
                }else {
                    text = rrh.getNomComplet() + " a déposé une demande explicative du retard.";
                }

            } else if (statusDg == StatusDemande.ACCEPTED) {
                notification.setOrigine(dg);
                destination.add(rrh);
                if (typeDemande == TypeDemande.CONGE){
                    text = dg.getNomComplet() + " a accepté votre demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = dg.getNomComplet() + " a accepté votre demande d'autorisation de sortie.";
                }else {
                    text = dg.getNomComplet() + " a accepté votre demande explicative du retard.";
                }
                sms = true;
            }else {
                notification.setOrigine(dg);
                destination.add(rrh);
                if (typeDemande == TypeDemande.CONGE){
                    text = dg.getNomComplet() + " a refusé votre demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = dg.getNomComplet() + " a refusé votre demande d'autorisation de sortie.";
                }else {
                    text = dg.getNomComplet() + " a refusé votre demande explicative du retard.";
                }
                sms = true;
            }
        } else if (demandeMaker == Role.CD) {
            if (statusRrh == StatusDemande.PENDING){
                notification.setOrigine(cd);
                destination.add(rrh);
                if (typeDemande == TypeDemande.CONGE){
                    text = cd.getNomComplet() + " a soumis une demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = cd.getNomComplet() + " demande une autorisation de sortie.";
                }else {
                    text = cd.getNomComplet() + " a déposé une demande explicative du retard.";
                }
            } else if (statusRrh == StatusDemande.REJECTED) {
                notification.setOrigine(rrh);
                destination.add(cd);
                if (typeDemande == TypeDemande.CONGE){
                    text = rrh.getNomComplet() + " a refusé votre demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = rrh.getNomComplet() + " a refusé votre demande d'autorisation de sortie.";
                }else {
                    text = rrh.getNomComplet() + " a refusé votre demande explicative du retard.";
                }
                sms = true;
            }else {
                if (statusRrh == StatusDemande.ACCEPTED && statusDg == StatusDemande.PENDING){
                    notification.setOrigine(cd);
                    destination.add(dg);
                    if (typeDemande == TypeDemande.CONGE){
                        text = "La demande de congé effectuée par " + cd.getNomComplet() + " attend votre approbation.";
                    } else if (typeDemande == TypeDemande.SORTIE) {
                        text = "La demande d'autorisation de sortie effectuée par " + cd.getNomComplet() + " attend votre approbation.";
                    }else {
                        text = "La demande explicative du retard effectuée par " + cd.getNomComplet() + " attend votre approbation.";
                    }

                } else if (statusRrh == StatusDemande.ACCEPTED && statusDg == StatusDemande.ACCEPTED) {
                    notification.setOrigine(dg);
                    destination.add(cd);

                    Notification second = new Notification();
                    List<User> dest = new ArrayList<User>();
                    dest.add(rrh);
                    List<User> seen = new ArrayList<User>();
                    second.setOrigine(dg);
                    second.setDestination(dest);
                    second.setVuePar(seen);
                    second.setDateCreation(LocalDateTime.now());

                    if (typeDemande == TypeDemande.CONGE){
                        text = dg.getNomComplet() + " a accepté votre demande de congé.";
                        second.setText(dg.getNomComplet() + " a accepté la demande de congé effectuée par" + demande.getUser().getNomComplet() + ".");
                    } else if (typeDemande == TypeDemande.SORTIE) {
                        text = dg.getNomComplet() + " a accepté votre demande d'autorisation de sortie.";
                        second.setText(dg.getNomComplet() + " a accepté la demande d'autorisation de sortie effectuée par" + demande.getUser().getNomComplet() + ".");
                    }else {
                        text = dg.getNomComplet() + " a accepté votre demande explicative du retard.";
                        second.setText(dg.getNomComplet() + " a accepté la demande explicative du retard effectuée par" + demande.getUser().getNomComplet() + ".");
                    }
                    //TODO notification2222222222222222 bch notifi beha RRH !!!!!!!!!!!!!!
                    sms = true;
                    notificationRepository.save(second);

                }else {
                    notification.setOrigine(dg);
                    destination.add(cd);

                    Notification second = new Notification();
                    List<User> dest = new ArrayList<User>();
                    dest.add(rrh);
                    List<User> seen = new ArrayList<User>();
                    second.setOrigine(dg);
                    second.setDestination(dest);
                    second.setVuePar(seen);
                    second.setDateCreation(LocalDateTime.now());

                    if (typeDemande == TypeDemande.CONGE){
                        text = dg.getNomComplet() + " a refusé votre demande de congé.";
                        second.setText(dg.getNomComplet() + " a refusé la demande de congé effectuée par" + demande.getUser().getNomComplet() + ".");
                    } else if (typeDemande == TypeDemande.SORTIE) {
                        text = dg.getNomComplet() + " a refusé votre demande d'autorisation de sortie.";
                        second.setText(dg.getNomComplet() + " a refusé la demande d'autorisation de sortie effectuée par" + demande.getUser().getNomComplet() + ".");
                    }else {
                        text = dg.getNomComplet() + " a refusé votre demande explicative du retard.";
                        second.setText(dg.getNomComplet() + " a refusé la demande explicative du retard effectuée par" + demande.getUser().getNomComplet() + ".");
                    }
                    //TODO notification2222222222222222 bch notifi beha RRH !!!!!!!!!!!!!!
                    sms = true;
                    notificationRepository.save(second);
                }
            }
        } else {
            if (statusCd == StatusDemande.PENDING && statusRrh == StatusDemande.PENDING){
                notification.setOrigine(demande.getUser());
                destination.add(rrh);
                destination.add(cd);
                if (typeDemande == TypeDemande.CONGE){
                    text = demande.getUser().getNomComplet() + " a soumis une demande de congé.";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = demande.getUser().getNomComplet() + " demande une autorisation de sortie.";
                }else {
                    text = demande.getUser().getNomComplet() + " a déposé une demande explicative du retard.";
                }
            } else if (statusCd == StatusDemande.ACCEPTED && statusRrh == StatusDemande.PENDING) {
                notification.setOrigine(cd);
                destination.add(rrh);
                if (typeDemande == TypeDemande.CONGE){
                    text = cd.getNomComplet() + " a accepté la demande de congé effectuée par " + demande.getUser().getNomComplet() + ".";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = cd.getNomComplet() + " a accepté la demande d'autorisation de sortie effectuée par " + demande.getUser().getNomComplet() + ".";
                }else {
                    text = cd.getNomComplet() + " a accepté la demande explicative du retard effectuée par " + demande.getUser().getNomComplet() + ".";
                }

            } else if (statusCd == StatusDemande.PENDING && statusRrh == StatusDemande.ACCEPTED) {
                notification.setOrigine(rrh);
                destination.add(cd);
                if (typeDemande == TypeDemande.CONGE){
                    text = rrh.getNomComplet() + " a accepté la demande de congé effectuée par " + demande.getUser().getNomComplet() + ".";
                } else if (typeDemande == TypeDemande.SORTIE) {
                    text = rrh.getNomComplet() + " a accepté la demande d'autorisation de sortie effectuée par " + demande.getUser().getNomComplet() + ".";
                }else {
                    text = rrh.getNomComplet() + " a accepté la demande explicative du retard effectuée par " + demande.getUser().getNomComplet() + ".";
                }

            } else if (statusCd == StatusDemande.REJECTED || statusRrh == StatusDemande.REJECTED) {
                if (statusCd == StatusDemande.REJECTED){
                    notification.setOrigine(cd);
                    destination.add(demande.getUser());

                    Notification second = new Notification();
                    List<User> dest = new ArrayList<User>();
                    dest.add(rrh);
                    List<User> seen = new ArrayList<User>();
                    second.setOrigine(dg);
                    second.setDestination(dest);
                    second.setVuePar(seen);
                    second.setDateCreation(LocalDateTime.now());

                    if (typeDemande == TypeDemande.CONGE){
                        text = cd.getNomComplet() + " a refusé votre demande de congé.";
                        second.setText(cd.getNomComplet() + " a refusé la demande de congé effectuée par" + demande.getUser().getNomComplet() + ".");
                    } else if (typeDemande == TypeDemande.SORTIE) {
                        text = cd.getNomComplet() + " a refusé votre demande d'autorisation de sortie.";
                        second.setText(cd.getNomComplet() + " a refusé la demande d'autorisation de sortie effectuée par" + demande.getUser().getNomComplet() + ".");
                    }else {
                        text = cd.getNomComplet() + " a refusé votre demande explicative du retard.";
                        second.setText(cd.getNomComplet() + " a refusé la demande explicative du retard effectuée par" + demande.getUser().getNomComplet() + ".");
                    }
                    //TODO notification2222222222222222 bch notifi beha RRH !!!!!!!!!!!!!!
                    sms = true;
                    notificationRepository.save(second);
                }else {
                    notification.setOrigine(rrh);
                    destination.add(demande.getUser());

                    Notification second = new Notification();
                    List<User> dest = new ArrayList<User>();
                    dest.add(cd);
                    List<User> seen = new ArrayList<User>();
                    second.setOrigine(dg);
                    second.setDestination(dest);
                    second.setVuePar(seen);
                    second.setDateCreation(LocalDateTime.now());

                    if (typeDemande == TypeDemande.CONGE){
                        text = rrh.getNomComplet() + " a refusé votre demande de congé.";
                        second.setText(rrh.getNomComplet() + " a refusé la demande de congé effectuée par" + demande.getUser().getNomComplet() + ".");
                    } else if (typeDemande == TypeDemande.SORTIE) {
                        text = rrh.getNomComplet() + " a refusé votre demande d'autorisation de sortie.";
                        second.setText(rrh.getNomComplet() + " a refusé la demande d'autorisation de sortie effectuée par" + demande.getUser().getNomComplet() + ".");
                    }else {
                        text = rrh.getNomComplet() + " a refusé votre demande explicative du retard.";
                        second.setText(rrh.getNomComplet() + " a refusé la demande explicative du retard effectuée par" + demande.getUser().getNomComplet() + ".");
                    }

                    //TODO notification2222222222222222 bch notifi beha RRH !!!!!!!!!!!!!!
                    sms = true;
                    notificationRepository.save(second);
                }
            }else{
                if (statusDg == StatusDemande.PENDING){
                    notification.setOrigine(demande.getUser());
                    destination.add(dg);
                    if (typeDemande == TypeDemande.CONGE){
                        text = "La demande de congé effectuée par " + demande.getUser().getNomComplet() + " attend votre approbation.";
                    } else if (typeDemande == TypeDemande.SORTIE) {
                        text = "La demande d'autorisation de sortie effectuée par " + demande.getUser().getNomComplet() + " attend votre approbation.";
                    }else {
                        text = "La demande explicative du retard effectuée par " + demande.getUser().getNomComplet() + " attend votre approbation.";
                    }
                } else if (statusDg == StatusDemande.REJECTED) {
                    notification.setOrigine(dg);
                    destination.add(demande.getUser());

                    Notification second = new Notification();
                    List<User> dest = new ArrayList<User>();
                    dest.add(cd);
                    dest.add(rrh);
                    List<User> seen = new ArrayList<User>();
                    second.setOrigine(dg);
                    second.setDestination(dest);
                    second.setVuePar(seen);
                    second.setDateCreation(LocalDateTime.now());


                    if (typeDemande == TypeDemande.CONGE){
                        text = dg.getNomComplet() + " a refusé votre demande de congé.";
                        second.setText(dg.getNomComplet() + " a refusé la demande de congé effectuée par" + demande.getUser().getNomComplet() + ".");
                    } else if (typeDemande == TypeDemande.SORTIE) {
                        text = dg.getNomComplet() + " a refusé votre demande d'autorisation de sortie.";
                        second.setText(dg.getNomComplet() + " a refusé la demande d'autorisation de sortie effectuée par" + demande.getUser().getNomComplet() + ".");
                    }else {
                        text = dg.getNomComplet() + " a refusé votre demande explicative du retard.";
                        second.setText(dg.getNomComplet() + " a refusé la demande explicative du retard effectuée par" + demande.getUser().getNomComplet() + ".");
                    }
                    sms = true;
                    notificationRepository.save(second);
                }else {
                    notification.setOrigine(dg);
                    destination.add(demande.getUser());

                    Notification second = new Notification();
                    List<User> dest = new ArrayList<User>();
                    dest.add(cd);
                    dest.add(rrh);
                    List<User> seen = new ArrayList<User>();
                    second.setOrigine(dg);
                    second.setDestination(dest);
                    second.setVuePar(seen);
                    second.setDateCreation(LocalDateTime.now());

                    if (typeDemande == TypeDemande.CONGE){
                        text = dg.getNomComplet() + " a accepté votre demande de congé.";
                        second.setText(dg.getNomComplet() + " a accepté la demande de congé effectuée par" + demande.getUser().getNomComplet() + ".");
                    } else if (typeDemande == TypeDemande.SORTIE) {
                        text = dg.getNomComplet() + " a accepté votre demande d'autorisation de sortie.";
                        second.setText(dg.getNomComplet() + " a accepté la demande d'autorisation de sortie effectuée par" + demande.getUser().getNomComplet() + ".");
                    }else {
                        text = dg.getNomComplet() + " a accepté votre demande explicative du retard.";
                        second.setText(dg.getNomComplet() + " a accepté la demande explicative du retard effectuée par" + demande.getUser().getNomComplet() + ".");
                    }
                    sms = true;
                    notificationRepository.save(second);
                }
            }
        }

        notification.setText(text);

        List<User> vuePar = new ArrayList<User>();
        notification.setVuePar(vuePar);
        notification.setDestination(destination);

        notification.setDateCreation(LocalDateTime.now());

        Notification n = notificationRepository.save(notification);
        if (sms){
            for (User u : destination){
                smsUtil.sendSms(text, u.getTelephone());
            }
        }


        NotificationDto nDto = new NotificationDto();

        nDto.setOrigineNom(n.getOrigine().getNomComplet());
        nDto.setOrigineImgPath(n.getOrigine().getImgPath());
        nDto.setVue(false);
        nDto.setId(n.getId());
        nDto.setText(n.getText());
        nDto.setDateCreation(n.getDateCreation());


        return nDto;
    }

    @Override
    public List<NotificationDto> getNotifications() throws UserException {
        User currentUser = userService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findAll();
        if(notifications.isEmpty()) {
            List<NotificationDto> dtoList = new ArrayList<>();
            return dtoList;
        }else {
            List<NotificationDto> dtoList = new ArrayList<>();
            for(Notification notif : notifications) {
                if (notif.getDestination().contains(currentUser)){
                    NotificationDto notifDto = new NotificationDto();
                    notifDto.setOrigineNom(notif.getOrigine().getNomComplet());
                    notifDto.setOrigineImgPath(notif.getOrigine().getImgPath());
                    if (notif.getVuePar().contains(currentUser)) {
                        notifDto.setVue(true);
                    }else {
                        notifDto.setVue(false);
                    }

                    notifDto.setId(notif.getId());
                    notifDto.setText(notif.getText());
                    notifDto.setDateCreation(notif.getDateCreation());


                    dtoList.add(notifDto);
                }
            }
            return dtoList;
        }
    }

    @Override
    public List<NotificationDto> seeAllNotifications() throws UserException {
        User currentUser = userService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findAll();
        if(notifications.isEmpty()) {
            List<NotificationDto> dtoList = new ArrayList<>();
            return dtoList;
        }else {
            List<NotificationDto> dtoList = new ArrayList<>();
            for(Notification notif : notifications) {
                if (notif.getDestination().contains(currentUser)){
                    if(!notif.getVuePar().contains(currentUser)){
                        List<User> seen = notif.getVuePar();
                        seen.add(currentUser);
                        notif.setVuePar(seen);
                        Notification n = notificationRepository.save(notif);

                        NotificationDto notifDto = new NotificationDto();
                        notifDto.setOrigineNom(n.getOrigine().getNomComplet());
                        notifDto.setOrigineImgPath(n.getOrigine().getImgPath());
                        if (n.getVuePar().contains(currentUser)) {
                            notifDto.setVue(true);
                        }else {
                            notifDto.setVue(false);
                        }
                        notifDto.setId(n.getId());
                        notifDto.setText(n.getText());
                        notifDto.setDateCreation(n.getDateCreation());
                        dtoList.add(notifDto);
                    }
                }
            }
            return dtoList;
        }
    }

    @Override
    public List<NotificationDto> clearNotifications() throws UserException {
        User currentUser = userService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findAll();
        if(notifications.isEmpty()) {
            List<NotificationDto> dtoList = new ArrayList<>();
            return dtoList;
        }else {
            List<NotificationDto> dtoList = new ArrayList<>();
            for(Notification notif : notifications) {
                if (notif.getDestination().contains(currentUser) && notif.getVuePar().contains(currentUser)){
                    notif.getDestination().remove(currentUser);
                    notif.getVuePar().remove(currentUser);
                    if (notif.getDestination().size() > 0){
                        notificationRepository.save(notif);
                    }else {
                        notificationRepository.delete(notif);
                    }


                    NotificationDto notifDto = new NotificationDto();
                    notifDto.setOrigineNom(notif.getOrigine().getNomComplet());
                    notifDto.setOrigineImgPath(notif.getOrigine().getImgPath());
                    if (notif.getVuePar().contains(currentUser)) {
                        notifDto.setVue(true);
                    }else {
                        notifDto.setVue(false);
                    }
                    notifDto.setId(notif.getId());
                    notifDto.setText(notif.getText());
                    notifDto.setDateCreation(notif.getDateCreation());


                    dtoList.add(notifDto);
                }
            }
            return dtoList;
        }
    }

    @Override
    public NotificationDto cancelDemandeNotification(User origine, Demande demande) throws UserException {
        List<Departement> listRH = departementRepository.findAll()
                .stream().filter((dept) -> dept.getType() == TypeDepartement.RH)
                .collect(Collectors.toList());
        List<Departement> listD = departementRepository.findAll()
                .stream().filter((dept) -> dept.getType() == TypeDepartement.DIRECTION)
                .collect(Collectors.toList());

        User dg = listD.get(0).getChefDepartement();
        User rrh = listRH.get(0).getChefDepartement();
        User cd = demande.getUser().getDepartement().getChefDepartement();
        Notification notification = new Notification();

        List<User> destination = new ArrayList<User>();
        String text = "";
        if (demande.getType() == TypeDemande.CONGE){text = demande.getUser().getNomComplet() + " a annulé sa demande de congé.";}
        if (demande.getType() == TypeDemande.SORTIE){text = demande.getUser().getNomComplet() + " a annulé sa demande d'autorisation de sortie.";}
        if (demande.getType() == TypeDemande.RETARD){text = demande.getUser().getNomComplet() + " a annulé sa demande d'explication de retard";}


        Role demandeMaker = demande.getUser().getRole();
        StatusDemande statusCd = demande.getStatusCd();
        StatusDemande statusRrh = demande.getStatusRrh();
        StatusDemande statusDg = demande.getStatusDg();
        TypeDemande typeDemande = demande.getType();
        if (demandeMaker == Role.DG){ destination.add(rrh); }
        if (demandeMaker == Role.RRH){ destination.add(dg); }
        if (demandeMaker == Role.CD){ destination.add(rrh); }
        if (demandeMaker == Role.USER){
            if (statusCd == StatusDemande.ACCEPTED && statusRrh == StatusDemande.ACCEPTED){
                destination.add(dg);
                destination.add(rrh);
                destination.add(cd);
            }else {
                destination.add(rrh);
                destination.add(cd);
            }
        }
        notification.setText(text);

        List<User> vuePar = new ArrayList<User>();
        notification.setOrigine(demande.getUser());
        notification.setVuePar(vuePar);
        notification.setDestination(destination);

        notification.setDateCreation(LocalDateTime.now());

        Notification n = notificationRepository.save(notification);

        if (statusCd == StatusDemande.ACCEPTED && statusRrh == StatusDemande.ACCEPTED && statusDg == StatusDemande.ACCEPTED){
            for (User u : destination){
                smsUtil.sendSms(text, u.getTelephone());
            }
        }

        NotificationDto nDto = new NotificationDto();

        nDto.setOrigineNom(n.getOrigine().getNomComplet());
        nDto.setOrigineImgPath(n.getOrigine().getImgPath());
        nDto.setVue(false);
        nDto.setId(n.getId());
        nDto.setText(n.getText());
        nDto.setDateCreation(n.getDateCreation());


        return nDto;
    }
}
