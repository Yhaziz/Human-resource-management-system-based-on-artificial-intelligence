package com.smarttech.SmartRH.AppResouces.Services;

import com.smarttech.SmartRH.AppResouces.Exceptions.DemandeException;
import com.smarttech.SmartRH.AppResouces.Exceptions.PermissionException;
import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.*;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.StatusDemande;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDemande;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Attachment;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Demande;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import com.smarttech.SmartRH.AppResouces.Repository.DemandeRepository;
import com.smarttech.SmartRH.AppResouces.Repository.UserRepository;
import com.smarttech.SmartRH.AppResouces.Services.interfaces.DemandeService;
import com.smarttech.SmartRH.AppUtils.FileUpload.FileUploadUtil;
import com.smarttech.SmartRH.AppUtils.Sms.SmsUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class DemandeServiceImpl implements DemandeService {
    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private AttachmentServiceImpl attachmentService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SoldeServiceImpl soldeService;
    @Autowired
    private NotificationServiceImpl notificationService;
    @Autowired
    private SmsUtil smsUtil;

    @Override
    public DemandeDto addDemande(DemandeDto dto, List<MultipartFile> files) throws DemandeException, IOException, UserException {
        User currentUser = userService.getCurrentUser();
        if (currentUser.isIndependant()) throw new DemandeException("Your account is set to independent");
        Demande demande = new Demande();
        demande.setUser(currentUser);
        demande.setType(dto.getType());
        demande.setCategorie(dto.getCategorie());
        demande.setMotif(dto.getMotif());
        demande.setDebut(dto.getDebut());
        demande.setFin(dto.getFin());

        if (currentUser.getRole() == Role.DG){
            demande.setStatusCd(StatusDemande.ACCEPTED);
            demande.setStatusRrh(StatusDemande.PENDING);
            demande.setStatusDg(StatusDemande.ACCEPTED);
        } else if (currentUser.getRole() == Role.RRH) {
            demande.setStatusCd(StatusDemande.ACCEPTED);
            demande.setStatusRrh(StatusDemande.ACCEPTED);
            demande.setStatusDg(StatusDemande.PENDING);
        } else if (currentUser.getRole() == Role.CD) {
            demande.setStatusCd(StatusDemande.ACCEPTED);
            demande.setStatusRrh(StatusDemande.PENDING);
            demande.setStatusDg(StatusDemande.PENDING);
        }else {
            demande.setStatusCd(StatusDemande.PENDING);
            demande.setStatusRrh(StatusDemande.PENDING);
            demande.setStatusDg(StatusDemande.PENDING);
        }

        demande.setCanceled(false);
        demande.setDateCreation(LocalDateTime.now());

        Demande demObj = demandeRepository.save(demande);
        if(files.size() > 0){
            List<Attachment> newList = new ArrayList<Attachment>();
            for(MultipartFile file : files) {
                Attachment att = attachmentService.addAttachment(file,demObj.getId());
                newList.add(att);
            }
            demande.setAttachments(newList);
        }
        Demande demObject = demandeRepository.save(demObj);
        notificationService.addNotification(demObject.getUser(), demObject);
        DemandeDto demDto = new DemandeDto();
        demDto.setId(demObject.getId());
        demDto.setUserId(demObject.getUser().getId());
        demDto.setType(demObject.getType());
        demDto.setCategorie(demObject.getCategorie());
        demDto.setMotif(demObject.getMotif());
        demDto.setDebut(demObject.getDebut());
        demDto.setFin(demObject.getFin());
        demDto.setStatusCd(demObject.getStatusCd());
        demDto.setStatusRrh(demObject.getStatusRrh());
        demDto.setStatusDg(demObject.getStatusDg());
        demDto.setDateCreation(demObject.getDateCreation());
        demDto.setDateReponse(demObject.getDateReponse());
        demDto.setNoteResponse(demObject.getNoteResponse());
        demDto.setCanceled(demObject.isCanceled());

        List<String> attachments = new ArrayList<String>();
        if (Objects.nonNull(demObject.getAttachments())){
            if(demObject.getAttachments().size() > 0){
                for (Attachment x : demObject.getAttachments()){
                    attachments.add(x.getPath());
                }
            }
        }
        demDto.setAttachments(attachments);
        return demDto;
    }

    @Override
    public DemandeDto deleteDemande(Long demId) throws DemandeException, PermissionException, IOException {
        User currentUser = userService.getCurrentUser();
        Demande targetDemande = demandeRepository.findById(demId).orElseThrow(() -> new DemandeException("Demande does not exist"));
        if (currentUser.getRole() != Role.DG) {
            throw new PermissionException("No Permission");
        }else {
            if (Objects.nonNull(targetDemande.getAttachments())){
                if (targetDemande.getAttachments().size() > 0){
                    for (Attachment x : targetDemande.getAttachments()){
                        FileUploadUtil.deleteFile(x.getPath());
                    }
                }
            }

            demandeRepository.delete(targetDemande);
        }
        DemandeDto demDto = modelMapper.map(targetDemande, DemandeDto.class);
        demDto.setUserId(targetDemande.getUser().getId());

        List<String> attachments = new ArrayList<String>();
        for (Attachment x : targetDemande.getAttachments()){
            attachments.add(x.getPath());
        }
        demDto.setAttachments(attachments);
        return demDto;
    }




    @Override
    public DemandeDto acceptDemande(Long demId) throws DemandeException, PermissionException, UserException {
        User currentUser = userService.getCurrentUser();
        Demande targetDemande = demandeRepository.findById(demId).orElseThrow(() -> new DemandeException("Demande does not exist"));
        if (currentUser.getRole() == Role.CD) {
            targetDemande.setStatusCd(StatusDemande.ACCEPTED);
        } else if (currentUser.getRole() == Role.RRH) {
            targetDemande.setStatusRrh(StatusDemande.ACCEPTED);
        } else if (currentUser.getRole() == Role.DG){
            targetDemande.setStatusDg(StatusDemande.ACCEPTED);
            targetDemande.setDateReponse(LocalDateTime.now());
        }else {
            throw new PermissionException("No Permission");
        }
        Demande newDemande = demandeRepository.save(targetDemande);
        notificationService.addNotification(newDemande.getUser(), newDemande);
        if (targetDemande.getStatusCd() == StatusDemande.ACCEPTED && targetDemande.getStatusRrh() == StatusDemande.ACCEPTED && targetDemande.getStatusDg() == StatusDemande.ACCEPTED ){
            if (newDemande.getType() == TypeDemande.CONGE){
                soldeService.subtractFromSoldeConge(newDemande.getUser().getId(), (int) Duration.between(newDemande.getDebut(), newDemande.getFin()).toDays());
            }

            if (newDemande.getType() == TypeDemande.SORTIE){
                soldeService.subtractFromSoldeSortie(newDemande.getUser().getId(), (int) Duration.between(newDemande.getDebut(), newDemande.getFin()).toMinutes());
            }
        }

        DemandeDto demDto = new DemandeDto();
        demDto.setDebut(newDemande.getDebut());
        demDto.setFin(newDemande.getFin());
        demDto.setCategorie(newDemande.getCategorie());
        demDto.setDateCreation(newDemande.getDateCreation());
        demDto.setDateReponse(newDemande.getDateReponse());
        demDto.setType(newDemande.getType());
        demDto.setMotif(newDemande.getMotif());
        demDto.setNoteResponse(newDemande.getNoteResponse());
        demDto.setStatusCd(newDemande.getStatusCd());
        demDto.setStatusDg(newDemande.getStatusDg());
        demDto.setStatusRrh(newDemande.getStatusRrh());
        demDto.setUserId(newDemande.getUser().getId());
        demDto.setId(newDemande.getId());
        demDto.setCanceled(newDemande.isCanceled());

        List<String> attachments = new ArrayList<String>();
        for (Attachment x : newDemande.getAttachments()){
            attachments.add(x.getPath());
        }
        demDto.setAttachments(attachments);
        return demDto;
    }

    @Override
    public DemandeDto rejectDemande(Long demId, String note) throws DemandeException, PermissionException, UserException {
        User currentUser = userService.getCurrentUser();
        Demande targetDemande = demandeRepository.findById(demId).orElseThrow(() -> new DemandeException("Demande does not exist"));
        if (currentUser.getRole() == Role.CD)
        {
            targetDemande.setStatusCd(StatusDemande.REJECTED);
            targetDemande.setDateReponse(LocalDateTime.now());


            targetDemande.setNoteResponse(note);
        } else if (currentUser.getRole() == Role.RRH)
        {
            targetDemande.setStatusRrh(StatusDemande.REJECTED);
            targetDemande.setDateReponse(LocalDateTime.now());


            targetDemande.setNoteResponse(note);
        } else if (currentUser.getRole() == Role.DG)
        {
            targetDemande.setStatusDg(StatusDemande.REJECTED);
            targetDemande.setDateReponse(LocalDateTime.now());


            targetDemande.setNoteResponse(note);
        }else {
            throw new PermissionException("No Permission");
        }
        Demande demande = demandeRepository.save(targetDemande);
        notificationService.addNotification(demande.getUser(), demande);
        DemandeDto demDto = new DemandeDto();
        demDto.setDebut(demande.getDebut());
        demDto.setFin(demande.getFin());
        demDto.setCategorie(demande.getCategorie());
        demDto.setDateCreation(demande.getDateCreation());
        demDto.setDateReponse(demande.getDateReponse());
        demDto.setType(demande.getType());
        demDto.setMotif(demande.getMotif());
        demDto.setNoteResponse(demande.getNoteResponse());
        demDto.setStatusCd(demande.getStatusCd());
        demDto.setStatusDg(demande.getStatusDg());
        demDto.setStatusRrh(demande.getStatusRrh());
        demDto.setUserId(demande.getUser().getId());
        demDto.setId(demande.getId());
        demDto.setCanceled(demande.isCanceled());

        List<String> attachments = new ArrayList<String>();
        for (Attachment x : demande.getAttachments()){
            attachments.add(x.getPath());
        }
        demDto.setAttachments(attachments);
        return demDto;
    }

    @Override
    public DemandeDto cancelDemande(Long demId) throws DemandeException, PermissionException, UserException {
        User currentUser = userService.getCurrentUser();
        Demande targetDemande = demandeRepository.findById(demId).orElseThrow(() -> new DemandeException("Demande does not exist"));
        if (currentUser == targetDemande.getUser()){
            if (LocalDateTime.now().isBefore(targetDemande.getDebut())){
                if (targetDemande.getStatusCd() == StatusDemande.ACCEPTED && targetDemande.getStatusRrh() == StatusDemande.ACCEPTED && targetDemande.getStatusDg() == StatusDemande.ACCEPTED ){
                    if (targetDemande.getType() == TypeDemande.CONGE){
                        soldeService.addToSoldeConge(targetDemande.getUser().getId(), (int) Duration.between(targetDemande.getDebut(), targetDemande.getFin()).toDays());
                    }

                    if (targetDemande.getType() == TypeDemande.SORTIE){
                        soldeService.addToSoldeSortie(targetDemande.getUser().getId(), (int) Duration.between(targetDemande.getDebut(), targetDemande.getFin()).toMinutes());
                    }
                }
                targetDemande.setCanceled(true);
            }else{
                throw new DemandeException("Too Late To Cancel");
            }
        }else {
            throw new PermissionException("No Permission");
        }

        Demande demande = demandeRepository.save(targetDemande);
        if (demande.isCanceled()){
            if (demande.getStatusCd() == StatusDemande.ACCEPTED && demande.getStatusRrh() == StatusDemande.ACCEPTED && demande.getStatusDg() == StatusDemande.ACCEPTED ){
                User cdObject = new User();
                User rrhObject = new User();
                User dgObject = new User();
                String text = "";
                if (demande.getType() == TypeDemande.CONGE){text = demande.getUser().getNomComplet() + " a annulé sa demande de congé.";}
                if (demande.getType() == TypeDemande.SORTIE){text = demande.getUser().getNomComplet() + " a annulé sa demande d'autorisation de sortie.";}
                if (demande.getType() == TypeDemande.RETARD){text = demande.getUser().getNomComplet() + " a annulé sa demande d'explication de retard";}
                List<User> listUsers = userRepository.findAll();
                for (User u : listUsers){
                    if (u.getRole() == Role.DG){dgObject = u;}
                    if (u.getRole() == Role.RRH){rrhObject = u;}
                    if (u.getRole() == Role.CD && demande.getUser().getDepartement() == u.getDepartement()){cdObject = u;}
                }
                smsUtil.cancelSms(text, cdObject.getTelephone());
            }
        }
        notificationService.cancelDemandeNotification(demande.getUser(), demande);

        DemandeDto demDto = new DemandeDto();
        demDto.setDebut(demande.getDebut());
        demDto.setFin(demande.getFin());
        demDto.setCategorie(demande.getCategorie());
        demDto.setDateCreation(demande.getDateCreation());
        demDto.setDateReponse(demande.getDateReponse());
        demDto.setType(demande.getType());
        demDto.setMotif(demande.getMotif());
        demDto.setNoteResponse(demande.getNoteResponse());
        demDto.setStatusCd(demande.getStatusCd());
        demDto.setStatusDg(demande.getStatusDg());
        demDto.setStatusRrh(demande.getStatusRrh());
        demDto.setUserId(demande.getUser().getId());
        demDto.setId(demande.getId());
        demDto.setCanceled(demande.isCanceled());

        List<String> attachments = new ArrayList<String>();
        for (Attachment x : demande.getAttachments()){
            attachments.add(x.getPath());
        }
        demDto.setAttachments(attachments);



        return demDto;
    }









    @Override
    public List<DemandeDto> getCurrentUserDemandes() throws DemandeException {
        User currentUser = userService.getCurrentUser();
        List<Demande> demandes = demandeRepository.findAll();

        if(demandes.isEmpty()) {
            List<DemandeDto> dtoList = new ArrayList<>();
            return dtoList;
        }else {

            List<DemandeDto> dtoList = new ArrayList<>();

            for(Demande demObject : demandes) {
                if (currentUser == demObject.getUser()){
                    DemandeDto demDto = new DemandeDto();
                    demDto.setId(demObject.getId());
                    demDto.setUserId(demObject.getUser().getId());
                    demDto.setType(demObject.getType());
                    demDto.setCategorie(demObject.getCategorie());
                    demDto.setMotif(demObject.getMotif());
                    demDto.setDebut(demObject.getDebut());
                    demDto.setFin(demObject.getFin());
                    demDto.setStatusCd(demObject.getStatusCd());
                    demDto.setStatusRrh(demObject.getStatusRrh());
                    demDto.setStatusDg(demObject.getStatusDg());
                    demDto.setDateCreation(demObject.getDateCreation());
                    demDto.setDateReponse(demObject.getDateReponse());
                    demDto.setNoteResponse(demObject.getNoteResponse());
                    demDto.setCanceled(demObject.isCanceled());

                    List<String> attachments = new ArrayList<String>();
                    if (Objects.nonNull(demObject.getAttachments())){
                        if(demObject.getAttachments().size() > 0){
                            for (Attachment x : demObject.getAttachments()){
                                attachments.add(x.getPath());
                            }
                        }
                    }
                    demDto.setAttachments(attachments);

                    dtoList.add(demDto);
                }
            }
            return dtoList;
        }
    }

    @Override
    public List<DemandeDto> getTeamDemandes() {
        User currentUser = userService.getCurrentUser();
        List<Demande> demandes = demandeRepository.findAll();

        if(demandes.isEmpty()) {
            List<DemandeDto> dtoList = new ArrayList<>();
            return dtoList;
        }else {

            List<DemandeDto> dtoList = new ArrayList<>();

            for(Demande demande : demandes) {
                if ((currentUser.getDepartement() == demande.getUser().getDepartement()) && (demande.getStatusDg() == StatusDemande.ACCEPTED && demande.getStatusRrh() == StatusDemande.ACCEPTED && demande.getStatusCd() == StatusDemande.ACCEPTED)){

                    if (demande.getType() != TypeDemande.RETARD && LocalDateTime.now().isAfter(demande.getDebut()) && !demande.isCanceled()){
                        DemandeDto demDto = new DemandeDto();
                        demDto.setDebut(demande.getDebut());
                        demDto.setFin(demande.getFin());
                        demDto.setCategorie(demande.getCategorie());
                        demDto.setDateCreation(demande.getDateCreation());
                        demDto.setDateReponse(demande.getDateReponse());
                        demDto.setType(demande.getType());
                        demDto.setMotif(demande.getMotif());
                        demDto.setNoteResponse(demande.getNoteResponse());
                        demDto.setStatusCd(demande.getStatusCd());
                        demDto.setStatusDg(demande.getStatusDg());
                        demDto.setStatusRrh(demande.getStatusRrh());
                        demDto.setUserId(demande.getUser().getId());
                        demDto.setId(demande.getId());
                        demDto.setCanceled(demande.isCanceled());

                        List<String> attachments = new ArrayList<String>();
                        for (Attachment x : demande.getAttachments()){
                            attachments.add(x.getPath());
                        }
                        demDto.setAttachments(attachments);

                        dtoList.add(demDto);
                    }
                }
            }
            return dtoList;
        }
    }


    public List<DemandeDto> getDecidedDemandes() throws DemandeException {
        User currentUser = userService.getCurrentUser();
        List<Demande> demandes = demandeRepository.findAll();

        if(demandes.isEmpty()) {
            List<DemandeDto> dtoList = new ArrayList<>();
            return dtoList;

        }else {
            List<DemandeDto> dtoList = new ArrayList<>();
            if (currentUser.getRole() == Role.DG || currentUser.getRole() == Role.RRH){
                for(Demande demande : demandes) {
                    if (
    (demande.getStatusDg() == StatusDemande.ACCEPTED && demande.getStatusRrh() == StatusDemande.ACCEPTED && demande.getStatusCd() == StatusDemande.ACCEPTED)
                        ||
    (demande.getStatusDg() == StatusDemande.REJECTED || demande.getStatusRrh() == StatusDemande.REJECTED || demande.getStatusCd() == StatusDemande.REJECTED)
                        || demande.isCanceled()) {
                        if(demande.isCanceled()){
                            if (currentUser.getRole() == Role.DG){
                                if (demande.getStatusRrh() == StatusDemande.ACCEPTED && demande.getStatusCd() == StatusDemande.ACCEPTED){
                                    DemandeDto demDto = new DemandeDto();
                                    demDto.setDebut(demande.getDebut());
                                    demDto.setFin(demande.getFin());
                                    demDto.setCategorie(demande.getCategorie());
                                    demDto.setDateCreation(demande.getDateCreation());
                                    demDto.setDateReponse(demande.getDateReponse());
                                    demDto.setType(demande.getType());
                                    demDto.setMotif(demande.getMotif());
                                    demDto.setNoteResponse(demande.getNoteResponse());
                                    demDto.setStatusCd(demande.getStatusCd());
                                    demDto.setStatusDg(demande.getStatusDg());
                                    demDto.setStatusRrh(demande.getStatusRrh());
                                    demDto.setUserId(demande.getUser().getId());
                                    demDto.setId(demande.getId());
                                    demDto.setCanceled(demande.isCanceled());

                                    List<String> attachments = new ArrayList<String>();
                                    for (Attachment x : demande.getAttachments()){
                                        attachments.add(x.getPath());
                                    }
                                    demDto.setAttachments(attachments);

                                    dtoList.add(demDto);
                                }
                            } else {
                                DemandeDto demDto = new DemandeDto();
                                demDto.setDebut(demande.getDebut());
                                demDto.setFin(demande.getFin());
                                demDto.setCategorie(demande.getCategorie());
                                demDto.setDateCreation(demande.getDateCreation());
                                demDto.setDateReponse(demande.getDateReponse());
                                demDto.setType(demande.getType());
                                demDto.setMotif(demande.getMotif());
                                demDto.setNoteResponse(demande.getNoteResponse());
                                demDto.setStatusCd(demande.getStatusCd());
                                demDto.setStatusDg(demande.getStatusDg());
                                demDto.setStatusRrh(demande.getStatusRrh());
                                demDto.setUserId(demande.getUser().getId());
                                demDto.setId(demande.getId());
                                demDto.setCanceled(demande.isCanceled());

                                List<String> attachments = new ArrayList<String>();
                                for (Attachment x : demande.getAttachments()){
                                    attachments.add(x.getPath());
                                }
                                demDto.setAttachments(attachments);

                                dtoList.add(demDto);
                            }
                        }else {
                            DemandeDto demDto = new DemandeDto();
                            demDto.setDebut(demande.getDebut());
                            demDto.setFin(demande.getFin());
                            demDto.setCategorie(demande.getCategorie());
                            demDto.setDateCreation(demande.getDateCreation());
                            demDto.setDateReponse(demande.getDateReponse());
                            demDto.setType(demande.getType());
                            demDto.setMotif(demande.getMotif());
                            demDto.setNoteResponse(demande.getNoteResponse());
                            demDto.setStatusCd(demande.getStatusCd());
                            demDto.setStatusDg(demande.getStatusDg());
                            demDto.setStatusRrh(demande.getStatusRrh());
                            demDto.setUserId(demande.getUser().getId());
                            demDto.setId(demande.getId());
                            demDto.setCanceled(demande.isCanceled());

                            List<String> attachments = new ArrayList<String>();
                            for (Attachment x : demande.getAttachments()){
                                attachments.add(x.getPath());
                            }
                            demDto.setAttachments(attachments);

                            dtoList.add(demDto);
                        }

                    }
                }
            }
            if (currentUser.getRole() == Role.CD){
                for(Demande demande : demandes) {
                    if (demande.getUser().getDepartement() == currentUser.getDepartement()){
                        if ((demande.getStatusDg() == StatusDemande.ACCEPTED && demande.getStatusRrh() == StatusDemande.ACCEPTED && demande.getStatusCd() == StatusDemande.ACCEPTED) || (demande.getStatusDg() == StatusDemande.REJECTED || demande.getStatusRrh() == StatusDemande.REJECTED || demande.getStatusCd() == StatusDemande.REJECTED)){
                            DemandeDto demDto = new DemandeDto();
                            demDto.setDebut(demande.getDebut());
                            demDto.setFin(demande.getFin());
                            demDto.setCategorie(demande.getCategorie());
                            demDto.setDateCreation(demande.getDateCreation());
                            demDto.setDateReponse(demande.getDateReponse());
                            demDto.setType(demande.getType());
                            demDto.setMotif(demande.getMotif());
                            demDto.setNoteResponse(demande.getNoteResponse());
                            demDto.setStatusCd(demande.getStatusCd());
                            demDto.setStatusDg(demande.getStatusDg());
                            demDto.setStatusRrh(demande.getStatusRrh());
                            demDto.setUserId(demande.getUser().getId());
                            demDto.setId(demande.getId());
                            demDto.setCanceled(demande.isCanceled());

                            List<String> attachments = new ArrayList<String>();
                            for (Attachment x : demande.getAttachments()){
                                attachments.add(x.getPath());
                            }
                            demDto.setAttachments(attachments);

                            dtoList.add(demDto);
                        }
                    }
                }
            }


            return dtoList;
        }
    }


    public List<DemandeDto> getNotDecidedDemandes() throws DemandeException {
        User currentUser = userService.getCurrentUser();
        List<Demande> demandes = demandeRepository.findAll();

        if(demandes.isEmpty()) {
            List<DemandeDto> dtoList = new ArrayList<>();
            return dtoList;

        }else {
            List<DemandeDto> dtoList = new ArrayList<>();
            if (currentUser.getRole() == Role.DG){
                for(Demande demande : demandes) {
                    if (!demande.isCanceled()){
                        if (demande.getStatusDg() == StatusDemande.PENDING && demande.getStatusRrh() == StatusDemande.ACCEPTED && demande.getStatusCd() == StatusDemande.ACCEPTED) {
                        DemandeDto demDto = new DemandeDto();
                        demDto.setDebut(demande.getDebut());
                        demDto.setFin(demande.getFin());
                        demDto.setCategorie(demande.getCategorie());
                        demDto.setDateCreation(demande.getDateCreation());
                        demDto.setDateReponse(demande.getDateReponse());
                        demDto.setType(demande.getType());
                        demDto.setMotif(demande.getMotif());
                        demDto.setNoteResponse(demande.getNoteResponse());
                        demDto.setStatusCd(demande.getStatusCd());
                        demDto.setStatusDg(demande.getStatusDg());
                        demDto.setStatusRrh(demande.getStatusRrh());
                        demDto.setUserId(demande.getUser().getId());
                        demDto.setId(demande.getId());
                        demDto.setCanceled(demande.isCanceled());

                        List<String> attachments = new ArrayList<String>();
                        for (Attachment x : demande.getAttachments()){
                            attachments.add(x.getPath());
                        }
                        demDto.setAttachments(attachments);

                        dtoList.add(demDto);
                    }
                    }
                }
            }
            if (currentUser.getRole() == Role.RRH){
                for(Demande demande : demandes) {
                    if (!demande.isCanceled()){
                        if (demande.getStatusDg() != StatusDemande.REJECTED && demande.getStatusRrh() == StatusDemande.PENDING && demande.getStatusCd() != StatusDemande.REJECTED) {
                        DemandeDto demDto = new DemandeDto();
                        demDto.setDebut(demande.getDebut());
                        demDto.setFin(demande.getFin());
                        demDto.setCategorie(demande.getCategorie());
                        demDto.setDateCreation(demande.getDateCreation());
                        demDto.setDateReponse(demande.getDateReponse());
                        demDto.setType(demande.getType());
                        demDto.setMotif(demande.getMotif());
                        demDto.setNoteResponse(demande.getNoteResponse());
                        demDto.setStatusCd(demande.getStatusCd());
                        demDto.setStatusDg(demande.getStatusDg());
                        demDto.setStatusRrh(demande.getStatusRrh());
                        demDto.setCanceled(demande.isCanceled());

                        demDto.setId(demande.getId());
                        demDto.setUserId(demande.getUser().getId());

                        List<String> attachments = new ArrayList<String>();
                        for (Attachment x : demande.getAttachments()){
                            attachments.add(x.getPath());
                        }
                        demDto.setAttachments(attachments);

                        dtoList.add(demDto);
                    }
                    }
                }
            }
            if (currentUser.getRole() == Role.CD){
                for(Demande demande : demandes) {
                    if (!demande.isCanceled()){
                        if (demande.getUser().getDepartement() == currentUser.getDepartement()){
                        if (demande.getStatusDg() != StatusDemande.REJECTED && demande.getStatusRrh() != StatusDemande.REJECTED && demande.getStatusCd() == StatusDemande.PENDING) {
                            DemandeDto demDto = new DemandeDto();
                            demDto.setDebut(demande.getDebut());
                            demDto.setFin(demande.getFin());
                            demDto.setCategorie(demande.getCategorie());
                            demDto.setDateCreation(demande.getDateCreation());
                            demDto.setDateReponse(demande.getDateReponse());
                            demDto.setType(demande.getType());
                            demDto.setMotif(demande.getMotif());
                            demDto.setNoteResponse(demande.getNoteResponse());
                            demDto.setStatusCd(demande.getStatusCd());
                            demDto.setStatusDg(demande.getStatusDg());
                            demDto.setStatusRrh(demande.getStatusRrh());
                            demDto.setUserId(demande.getUser().getId());
                            demDto.setId(demande.getId());
                            demDto.setCanceled(demande.isCanceled());

                            List<String> attachments = new ArrayList<String>();
                            for (Attachment x : demande.getAttachments()){
                                attachments.add(x.getPath());
                            }
                            demDto.setAttachments(attachments);

                            dtoList.add(demDto);
                        }
                    }
                    }
                }
            }


            return dtoList;
        }
    }
}
