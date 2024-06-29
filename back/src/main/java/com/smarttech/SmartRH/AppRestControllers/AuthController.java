package com.smarttech.SmartRH.AppRestControllers;


import com.smarttech.SmartRH.AppResouces.Models.DTOs.*;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import com.smarttech.SmartRH.AppResouces.Repository.UserRepository;
import com.smarttech.SmartRH.AppResouces.Services.UserServiceImpl;
import com.smarttech.SmartRH.AppSecurity.AuthenticationService;
import com.smarttech.SmartRH.AppSecurity.JWT.JWTGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@CrossOrigin("*")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, AuthenticationService authenticationService, UserRepository userRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @Autowired
    UserServiceImpl userService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails)o;
        String username = userDetails.getUsername();

        User loggeduser = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user does not exist"));

        authenticationService.revokeAllTokenByUser(loggeduser);
        authenticationService.saveUserToken(token, loggeduser);

        return new ResponseEntity<>(new AuthResponseDTO(token, loggeduser.getRole()), HttpStatus.OK);
    }

    @GetMapping("whoAmI")
    public ResponseEntity<UserDto> whoAmI() {
        User currentUser = userService.getCurrentUser();
        UserDto userDto = modelMapper.map(currentUser, UserDto.class);

        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }

}