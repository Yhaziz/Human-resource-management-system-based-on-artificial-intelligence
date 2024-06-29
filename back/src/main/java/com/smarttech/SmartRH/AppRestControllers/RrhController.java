package com.smarttech.SmartRH.AppRestControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smarttech.SmartRH.AppConfig.PlatformConfig;
import com.smarttech.SmartRH.AppResouces.Exceptions.*;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.*;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import com.smarttech.SmartRH.AppResouces.Services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequestMapping("/RRH")
@RestController
public class RrhController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private DepartementServiceImpl departementService;
    @Autowired
    private DemandeServiceImpl demandeService;
    @Autowired
    private SoldeServiceImpl soldeService;
    @Autowired
    private NotificationServiceImpl notificationService;

    @Autowired
    private PlatformConfig platformConfig;


    /////////////////////////////////////////////////////////////////////////// Current User features :

    ////////// Settings
    @PatchMapping("/settings/updatePassword")
    public ResponseEntity<UpdatePasswordDto> updatePassword(@RequestBody UpdatePasswordDto dto) throws PasswordException{

        String message = userService.updatePassword(dto);

        return new ResponseEntity<UpdatePasswordDto>(dto,HttpStatus.OK);
    }
    @PutMapping(value = "/settings/updateProfile", consumes = {"multipart/form-data"})
    public ResponseEntity<ProfileDto> updateProfile(@RequestPart("data") String data, @RequestPart(value = "file", required = false) MultipartFile file) throws UserException, PermissionException, IOException {
        MultipartFile img = null;
        if(Objects.nonNull(file)){img=file;}
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileDto dto = objectMapper.readValue(data, ProfileDto.class);
        ProfileDto updatedProfileDto = userService.updateProfile(dto, img);

        return new ResponseEntity<ProfileDto>(updatedProfileDto, HttpStatus.OK);
    }

    ////////// Team
    @GetMapping("/team")
    public ResponseEntity<List<UserDto>> getMyTeam() throws UserException {

        List<UserDto> getUserDtos = userService.getMyTeam();

        return new ResponseEntity<List<UserDto>>(getUserDtos, HttpStatus.OK);
    }

    @GetMapping("/team/demandes")
    public ResponseEntity<List<DemandeDto>> getTeamDemandes() {

        List<DemandeDto> getDemandesList = demandeService.getTeamDemandes();

        return new ResponseEntity<List<DemandeDto>>(getDemandesList, HttpStatus.OK);
    }

    ////////// Solde
    @GetMapping("/solde")
    public ResponseEntity<SoldeDto> getMySolde() throws UserException {
        User currentuser = userService.getCurrentUser();
        SoldeDto soldeDto = soldeService.getSolde(currentuser.getId());

        return new ResponseEntity<SoldeDto>(soldeDto, HttpStatus.OK);
    }


    ////////// Notifications
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> getNotifications() throws UserException {

        List<NotificationDto> lista = notificationService.getNotifications();

        return new ResponseEntity<List<NotificationDto>>(lista, HttpStatus.OK);
    }
    @PatchMapping("/notifications/seeAll")
    public ResponseEntity<List<NotificationDto>> seeAllNotifications() throws UserException {

        List<NotificationDto> lista = notificationService.seeAllNotifications();

        return new ResponseEntity<List<NotificationDto>>(lista, HttpStatus.OK);
    }


    ////////// Demandes
    @GetMapping("/mes-demandes")
    public ResponseEntity<List<DemandeDto>> getMesDemandes()  throws DemandeException{

        List<DemandeDto> getDemandesList = demandeService.getCurrentUserDemandes();

        return new ResponseEntity<List<DemandeDto>>(getDemandesList, HttpStatus.OK);
    }
    @PostMapping(value = "/mes-demandes/add", consumes = {"multipart/form-data"})
    public ResponseEntity<DemandeDto> addDemande(@RequestPart("data") String dataJson, @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException, DemandeException, UserException {
        List<MultipartFile> fs = new ArrayList<MultipartFile>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Register the module
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: to use ISO8601 format

        DemandeDto demande = objectMapper.readValue(dataJson, DemandeDto.class);
        if(Objects.nonNull(files)){fs = files;}
        DemandeDto demandeDto = demandeService.addDemande(demande, fs);

        return new ResponseEntity<DemandeDto>(demandeDto, HttpStatus.OK);
    }
    @PatchMapping("/mes-demandes/{id}/cancel")
    public ResponseEntity<DemandeDto> cancelDemande(@PathVariable("id") Long demId) throws PermissionException, UserException, DemandeException {
        DemandeDto dto = demandeService.cancelDemande(demId);

        return new ResponseEntity<DemandeDto>(dto, HttpStatus.OK);
    }

    ////////// AI
    @GetMapping("/isAIenabled")
    public ResponseEntity<Boolean> isAIenabled(){

        Boolean value = platformConfig.isAiFeature();

        return new ResponseEntity<Boolean>(value, HttpStatus.OK);
    }


    /////////////////////////////////////////////////////////////////////////// DG features :

    ////////// User Related Resources
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() throws UserException {

        List<UserDto> getUserDtos = userService.getAllUsers();

        return new ResponseEntity<List<UserDto>>(getUserDtos, HttpStatus.OK);
    }
    @PostMapping("/users/add")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto user) throws UserException, DepartementException{

        UserDto userDto = userService.addUser(user);

        return new ResponseEntity<UserDto>(userDto, HttpStatus.CREATED);
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long userId) throws UserException {
        UserDto userDto = userService.getUserById(userId);

        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }
    @PatchMapping("/users/{id}/resetPassword")
    public ResponseEntity<String> resetPassword(@PathVariable("id") Long userId) throws UserException, PermissionException {
        String message = userService.resetPassword(userId);

        return new ResponseEntity<String>(message,HttpStatus.OK);
    }
    @PutMapping("/users/{id}/update")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long userId, @RequestBody UserDto dto) throws PermissionException, UserException, DepartementException{

        UserDto updatedUserDto = userService.updateUser(userId, dto);

        return new ResponseEntity<UserDto>(updatedUserDto, HttpStatus.CREATED);
    }
    @DeleteMapping("/users/{id}/delete")
    public ResponseEntity<UserDto> deleteUser(@PathVariable("id") Long userId) throws UserException, PermissionException, IOException, DemandeException {

        UserDto deteledUserDto = userService.deleteUser(userId);

        return new ResponseEntity<UserDto>(deteledUserDto, HttpStatus.OK);
    }



    ////////// Department Related Resources
    @GetMapping("/departements")
    public ResponseEntity<List<DepartementDto>> getAllDepartements() throws DepartementException {

        List<DepartementDto> getDepartementDtos = departementService.getAllDepartements();

        return new ResponseEntity<List<DepartementDto>>(getDepartementDtos, HttpStatus.OK);
    }
    @PostMapping("/departements/add")
    public ResponseEntity<DepartementDto> addDepartement(@RequestBody DepartementDto dto) throws DepartementException, PermissionException{

        DepartementDto departementDto = departementService.addDepartement(dto);

        return new ResponseEntity<DepartementDto>(departementDto, HttpStatus.CREATED);
    }
    @GetMapping("/departements/{id}")
    public ResponseEntity<DepartementDto> getDepartementById(@PathVariable("id") Long deptId) throws DepartementException {
        DepartementDto departementDto = departementService.getDepartementById(deptId);

        return new ResponseEntity<DepartementDto>(departementDto, HttpStatus.OK);
    }
    @PutMapping("/departements/{id}/update")
    public ResponseEntity<DepartementDto> updateDepartement(@PathVariable("id") Long deptId, @RequestBody DepartementDto dto) throws DepartementException, PermissionException, UserException{

        DepartementDto updatedDepartementDto = departementService.updateDepartement(deptId, dto);

        return new ResponseEntity<DepartementDto>(updatedDepartementDto, HttpStatus.CREATED);
    }
    @DeleteMapping("/departements/{id}/delete")
    public ResponseEntity<DepartementDto> deleteDepartement(@PathVariable("id") Long deptId) throws DepartementException, PermissionException, UserException, IOException, DemandeException {

        DepartementDto deteledDepartementDto = departementService.deleteDepartement(deptId);

        return new ResponseEntity<DepartementDto>(deteledDepartementDto, HttpStatus.OK);
    }

    ////////// Demande Related Resources

    @GetMapping("/demandes")
    public ResponseEntity<List<DepartementDto>> getAllDemandes() throws DepartementException {

        List<DepartementDto> getDepartementDtos = departementService.getAllDepartements();

        return new ResponseEntity<List<DepartementDto>>(getDepartementDtos, HttpStatus.OK);
    }

    @GetMapping("/demandes/decided-by-all")
    public ResponseEntity<List<DemandeDto>> getDecidedDemandes() throws DemandeException {

        List<DemandeDto> lista = demandeService.getDecidedDemandes();

        return new ResponseEntity<List<DemandeDto>>(lista, HttpStatus.OK);
    }

    @GetMapping("/demandes/to-decide-by-me")
    public ResponseEntity<List<DemandeDto>> getNotDecidedDemandes() throws DemandeException {

        List<DemandeDto> lista = demandeService.getNotDecidedDemandes();

        return new ResponseEntity<List<DemandeDto>>(lista, HttpStatus.OK);
    }

    @PatchMapping("/demandes/{id}/reject")
    public ResponseEntity<DemandeDto> rejectDemande(@PathVariable("id") Long idDem, @RequestBody String note) throws PermissionException, DemandeException, UserException {

        DemandeDto dto = demandeService.rejectDemande(idDem, note);

        return new ResponseEntity<DemandeDto>(dto, HttpStatus.OK);
    }

    @PatchMapping("/demandes/{id}/accept")
    public ResponseEntity<DemandeDto> acceptDemande(@PathVariable("id") Long idDem) throws PermissionException, UserException, DemandeException {

        DemandeDto dto = demandeService.acceptDemande(idDem);

        return new ResponseEntity<DemandeDto>(dto, HttpStatus.OK);
    }


    ////////// Solde Related Resources
    @GetMapping("/solde/all")
    public ResponseEntity<List<SoldeDto>> getAllSolde() {
        List<SoldeDto> lista = soldeService.getAllSolde();

        return new ResponseEntity<List<SoldeDto>>(lista, HttpStatus.OK);
    }
    @PutMapping("/solde/{userId}/update")
    public ResponseEntity<SoldeDto> updateSolde(@PathVariable("userId") Long userId, @RequestBody SoldeDto dto) throws UserException {
        SoldeDto soldeDto = soldeService.updateSolde(userId, dto);

        return new ResponseEntity<SoldeDto>(soldeDto, HttpStatus.OK);
    }





}
