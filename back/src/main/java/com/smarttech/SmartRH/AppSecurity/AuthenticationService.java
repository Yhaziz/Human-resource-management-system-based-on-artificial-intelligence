package com.smarttech.SmartRH.AppSecurity;

import com.smarttech.SmartRH.AppResouces.Models.DTOs.AuthResponseDTO;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Token;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import com.smarttech.SmartRH.AppResouces.Repository.TokenRepository;
import com.smarttech.SmartRH.AppResouces.Repository.UserRepository;
import com.smarttech.SmartRH.AppSecurity.JWT.JWTGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {


    private final TokenRepository tokenRepository;

    public AuthenticationService(TokenRepository tokenRepository) {

        this.tokenRepository = tokenRepository;
    }


    public void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }
    public void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }
}