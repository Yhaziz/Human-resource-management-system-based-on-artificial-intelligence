package com.smarttech.SmartRH.AppResouces.Services;

import com.smarttech.SmartRH.AppResouces.Exceptions.*;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.ProfileDto;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.UpdatePasswordDto;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.UserDto;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import com.smarttech.SmartRH.AppResouces.Models.Entities.*;
import com.smarttech.SmartRH.AppResouces.Repository.*;
import com.smarttech.SmartRH.AppResouces.Services.interfaces.UserService;
import com.smarttech.SmartRH.AppUtils.FileUpload.FileUploadUtil;
import com.smarttech.SmartRH.AppUtils.Sms.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder pwd;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SoldeServiceImpl soldeService;
    @Autowired
    private SmsUtil smsUtil;

    // Current User features :
    @Override
    public User getCurrentUser() {

        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        if(o.equals("anonymousUser")) throw new RuntimeException("Please login first...");

        UserDetails userDetails = (UserDetails)o;


        String username = userDetails.getUsername();


        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user does not exist"));

    }

    @Override
    public String updatePassword(UpdatePasswordDto dto) throws PasswordException {
            User currentUser = getCurrentUser();

            if(!dto.getNewPassword().equals(dto.getConfirmPassword()))
                throw new PasswordException("Password confirmation does not match");

            if(!pwd.matches(dto.getCurrentPassword(), currentUser.getPassword()))
                throw new PasswordException("Current Password is incorrect");

            if(pwd.matches(dto.getNewPassword(), currentUser.getPassword()))
                throw new PasswordException("New password needs to be different");

            currentUser.setPassword(pwd.encode(dto.getNewPassword()));
            userRepository.save(currentUser);

            return "Password has been changed";
        }

    public ProfileDto updateProfile(ProfileDto dto, MultipartFile file) throws UserException, PermissionException, IOException {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(dto.getId())
                .orElseThrow(() -> new UserException("User does not exist with this id..."));
        if(currentUser.getCin() != targetUser.getCin()) throw new PermissionException("You don't have permission");

        if(!Objects.equals(targetUser.getTelephone(), dto.getTelephone())){
           if(userRepository.existsByTelephone(dto.getTelephone())) throw new UserException("PhoneNumber already exists");
           targetUser.setTelephone(dto.getTelephone());
        }
        if(Objects.nonNull(file)){
            if(Objects.isNull(targetUser.getImgPath())){
                String imgPath = FileUploadUtil.saveFile(file);
                targetUser.setImgPath(imgPath);
            }else {
                FileUploadUtil.deleteFile(targetUser.getImgPath());
                String imgPath = FileUploadUtil.saveFile(file);
                targetUser.setImgPath(imgPath);
            }

        }
        if(targetUser.getNomComplet() != dto.getNomComplet()){ targetUser.setNomComplet(dto.getNomComplet());}
        User user = userRepository.save(targetUser);
        ProfileDto profileDto = modelMapper.map(user, ProfileDto.class);
        return profileDto;

    }

    @Override
    public List<UserDto> getMyTeam() throws UserException {
        User currentUser = getCurrentUser();
        List<User> users = userRepository.findAll();

        if(users.isEmpty()) {
            throw new UserException("No Users Found");
        }else {

            List<UserDto> dtoList = new ArrayList<>();

            for(User user : users) {
                if (currentUser.getDepartement() == user.getDepartement()){
                    UserDto userDto = modelMapper.map(user, UserDto.class);

                    userDto.setDepartementId(user.getDepartement().getId());
                    userDto.setDepartementName(user.getDepartement().getName());

                    dtoList.add(userDto);
                }
            }

            return dtoList;
        }
    }


    // Admin features :
    @Override
    public String resetPassword(Long userId) throws UserException, PermissionException {
        User currentUser = getCurrentUser();
        Role role = currentUser.getRole();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User does not exist"));
        Role targetRole = targetUser.getRole();
        if(role != Role.DG && role != Role.RRH){
            throw new PermissionException("You don't have permission");
        }else{
            targetUser.setPassword(pwd.encode(targetUser.getCin()+"$mart"));
            userRepository.save(targetUser);
            smsUtil.passwordResetSms(targetUser.getTelephone());
            return "Password has been reset successfully";
        }
    }

    @Override
    public UserDto getUserById(Long userId) throws UserException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User does not exist with this id..."));

        UserDto userDto = modelMapper.map(user, UserDto.class);

        userDto.setDepartementId(user.getDepartement().getId());
        userDto.setDepartementName(user.getDepartement().getName());

        return userDto;
    }

    @Override
    public UserDto addUser(UserDto user) throws UserException, DepartementException {
        Departement departement = departementRepository.findById(user.getDepartementId())
                .orElseThrow(() -> new DepartementException("No department found with this Id"));
        if(userRepository.existsByCin(user.getCin())){
            throw new UserException("Cin already exists");
        } else if (userRepository.existsByTelephone(user.getTelephone())) {
            throw new UserException("Telephone already exists");
        } else {
            User newUser = new User();
            newUser.setCin(user.getCin());
            newUser.setUsername(user.getCin());
            newUser.setPassword(pwd.encode(user.getCin()+"$mart"));
            newUser.setNomComplet(user.getNomComplet());
            newUser.setQualification(user.getQualification());
            newUser.setTelephone(user.getTelephone());
            newUser.setDob(user.getDob());
            newUser.setDoj(user.getDoj());

            newUser.setSexe(user.getSexe());
            newUser.setRole(Role.USER);
            newUser.setEnabled(Boolean.TRUE);
            newUser.setIndependant(Boolean.FALSE);
            newUser.setDateCreation(LocalDateTime.now());

            newUser.setDepartement(departement);
            departement.getUsers().add(newUser);


            User userObj = userRepository.save(newUser);
            soldeService.addSolde(userObj.getId());

            return modelMapper.map(userObj, UserDto.class);

        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto dto) throws PermissionException, UserException, DepartementException{
        User currentUser = getCurrentUser();
        Role role = currentUser.getRole();
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new UserException("User does not exist"));
        Role targetRole = targetUser.getRole();
        if(((role != Role.DG) && (role != Role.RRH))){
            throw new PermissionException("You don't have permission");
        }else{
            if((role == Role.RRH) && (targetRole == Role.DG)) throw new PermissionException("You don't have permission");
            Departement d = departementRepository.findById(dto.getDepartementId()).orElse(null);
            if(!(targetUser.getCin().equals(dto.getCin())) && dto.getCin() != null) {
                if(userRepository.existsByCin(dto.getCin())){
                    throw new UserException("Cin already exists");
                }
                targetUser.setCin(dto.getCin());
                targetUser.setUsername(dto.getCin());
            }
            if(targetUser.getNomComplet() != dto.getNomComplet() && dto.getNomComplet() != null){ targetUser.setNomComplet(dto.getNomComplet());}
            if(targetUser.getQualification() != dto.getQualification() && dto.getQualification() != null){ targetUser.setQualification(dto.getQualification());}
            if(!(targetUser.getTelephone().equals(dto.getTelephone())) && dto.getTelephone() != null){
                if(userRepository.existsByTelephone(dto.getTelephone())){
                    throw new UserException("Telephone already exists");
                }
                targetUser.setTelephone(dto.getTelephone());
            }

            if(targetUser.getDob() != dto.getDob() && dto.getDob() != null){ targetUser.setDob(dto.getDob());}
            if(targetUser.getDoj() != dto.getDoj() && dto.getDoj() != null){ targetUser.setDoj(dto.getDoj());}
            if(targetUser.getSexe() != dto.getSexe() && dto.getSexe() != null){ targetUser.setSexe(dto.getSexe());}
            if(targetUser.getDepartement() != d && d != null){
                if (targetUser.getRole() == Role.DG || targetUser.getRole() == Role.RRH || targetUser.getRole() == Role.CD){
                    throw new UserException("You must change Department Chef First");
                }else {
                    targetUser.setDepartement(d);
                    d.getUsers().add(targetUser);
                }
            }
            if (targetUser.isIndependant() != dto.isIndependant()) { targetUser.setIndependant(dto.isIndependant()); }
            if ((targetUser.getRole() == Role.DG || targetUser.getRole() == Role.RRH) && targetUser.isEnabled() != dto.isEnabled()) {
                throw new UserException("This User can't be disabled !");
            }
            if(targetUser.isEnabled() != dto.isEnabled()){
                targetUser.setEnabled(dto.isEnabled());
            }
            User newUser = userRepository.save(targetUser);
            return modelMapper.map(newUser, UserDto.class);
        }
    }

    @Override
    public List<UserDto> getAllUsers() throws UserException {

        List<User> users = userRepository.findAll();

        if(users.isEmpty()) {
            throw new UserException("No Users Found");
        }else {

            List<UserDto> dtoList = new ArrayList<>();

            for(User user : users) {
                UserDto userDto = modelMapper.map(user, UserDto.class);

                userDto.setDepartementId(user.getDepartement().getId());
                userDto.setDepartementName(user.getDepartement().getName());

                dtoList.add(userDto);
            }

            return dtoList;
        }
    }


    @Override
    public UserDto deleteUser(Long userId) throws UserException, PermissionException, IOException, DemandeException {
        User currentUser = getCurrentUser();
        Role role = currentUser.getRole();
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new UserException("User does not exist"));
        Role targetRole = targetUser.getRole();
        if((targetRole == Role.DG) || (targetRole == Role.RRH)){
            throw new PermissionException("This User can't be Deleted");
        }else{
            if(targetRole == Role.CD) {
                Departement d = departementRepository.findById(targetUser.getDepartement().getId()).orElseThrow(() -> new UserException("Department does not exist"));
                d.setChefDepartement(null);
                departementRepository.save(d);
            }
            if (Objects.nonNull(targetUser.getImgPath())){
                FileUploadUtil.deleteFile(targetUser.getImgPath());
            }


            // Delete notifications where the user is in vuePar
            List<Notification> notificationsWithUserInViewPar = notificationRepository.findByVueParContaining(targetUser);
            for (Notification notification : notificationsWithUserInViewPar) {
                notification.getVuePar().remove(targetUser);
                notificationRepository.save(notification);
            }

            // Delete notifications where the user is in destination
            List<Notification> notificationsWithUserInDestination = notificationRepository.findByDestinationContaining(targetUser);
            for (Notification notification : notificationsWithUserInDestination) {
                notification.getDestination().remove(targetUser);
                if (notification.getDestination().size() > 0){
                    notificationRepository.save(notification);
                }else {
                    notificationRepository.delete(notification);
                }
            }

            // Delete notifications where the user is the origine
            List<Notification> notificationsWithUserAsOrigine = notificationRepository.findByOrigine(targetUser);
            for (Notification notification : notificationsWithUserAsOrigine) {
                notificationRepository.delete(notification);
            }

            List<Token> tokens = tokenRepository.findAllTheTokensByUser(targetUser.getId());
            for (Token token : tokens) {
                tokenRepository.delete(token);
            }

            List<Demande> dems = demandeRepository.findAllDemandesByUser(targetUser.getId());
            for (Demande dem : dems) {
                if (Objects.nonNull(dem.getAttachments())){
                    if (dem.getAttachments().size() > 0){
                        for (Attachment x : dem.getAttachments()){
                            FileUploadUtil.deleteFile(x.getPath());
                            attachmentRepository.delete(x);
                        }
                    }
                }
                demandeRepository.delete(dem);
            }

            soldeService.deleteSolde(targetUser.getId());


            userRepository.delete(targetUser);
            }
        //////////////////////////////////////////////////////////////////////

        UserDto userDto = modelMapper.map(targetUser, UserDto.class);

        userDto.setDepartementId(targetUser.getDepartement().getId());
        userDto.setDepartementName(targetUser.getDepartement().getName());

        return userDto;
    }


}
