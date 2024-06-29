package com.smarttech.SmartRH.AppResouces.Services.interfaces;

import com.smarttech.SmartRH.AppResouces.Exceptions.*;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.ProfileDto;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.UpdatePasswordDto;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.UserDto;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    // Current User features :
    public User getCurrentUser();
    public String updatePassword(UpdatePasswordDto dto) throws PasswordException;
    public ProfileDto updateProfile(ProfileDto dto, MultipartFile file) throws UserException, PermissionException, IOException;
    public List<UserDto> getMyTeam() throws UserException;



    // Admin features :
    public String resetPassword(Long userId) throws UserException, PermissionException;
    public UserDto getUserById(Long userId) throws UserException;

    public UserDto addUser(UserDto user) throws UserException, DepartementException;
    public UserDto updateUser(Long userId, UserDto dto) throws PermissionException, UserException, DepartementException;
    public List<UserDto> getAllUsers() throws UserException;
    public UserDto deleteUser(Long userId) throws UserException, PermissionException, IOException, DemandeException;
}
