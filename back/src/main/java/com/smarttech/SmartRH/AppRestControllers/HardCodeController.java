package com.smarttech.SmartRH.AppRestControllers;

import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Sexe;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDepartement;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Departement;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import com.smarttech.SmartRH.AppResouces.Repository.DepartementRepository;
import com.smarttech.SmartRH.AppResouces.Repository.UserRepository;
import com.smarttech.SmartRH.AppResouces.Services.SoldeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HardCodeController {
    @Autowired
    private PasswordEncoder pwd;
    @Autowired
    DepartementRepository departementRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SoldeServiceImpl soldeService;




    @GetMapping("/hardcode")
    public ResponseEntity<String> hardcode() throws UserException {
        if(!userRepository.existsByCin("11111111")) {
            List<Departement> listDirection = departementRepository.findAll()
                    .stream().filter((dept) -> dept.getType() == TypeDepartement.DIRECTION)
                    .collect(Collectors.toList());
            if(listDirection.size() == 0) {
                Departement direction = new Departement();
                direction.setName("Departement de Direction");
                direction.setType(TypeDepartement.DIRECTION);
                Departement newD = departementRepository.save(direction);
                Long idD = newD.getId();


                User dg = new User();
                dg.setCin("11111111");
                dg.setNomComplet("Semi Gharbi");
                dg.setQualification("Directeur Général");
                dg.setTelephone("94322249");
                dg.setSexe(Sexe.MALE);
                dg.setRole(Role.DG);
                dg.setUsername("11111111");
                dg.setPassword(pwd.encode("11111111$mart"));
                dg.setDepartement(newD); // Departement RH
                dg.setDoj(LocalDate.of(2020, 1, 1));
                dg.setDob(LocalDate.of(2000, 1, 1));
                dg.setEnabled(true);
                dg.setIndependant(true);
                dg.setDateCreation(LocalDateTime.now());

                User newDG = userRepository.save(dg);
                soldeService.addSolde(newDG.getId());
                Departement xdD = departementRepository.findById(idD)
                        .orElseThrow(() -> new RuntimeException("Departement DIRECTION does not exist"));
                xdD.setChefDepartement(newDG);
                Departement xxxxxxd = departementRepository.save(xdD);
            }


            List<Departement> listRH = departementRepository.findAll()
                    .stream().filter((dept) -> dept.getType() == TypeDepartement.RH)
                    .collect(Collectors.toList());
            if(listRH.size() == 0) {
                Departement rh = new Departement();
                rh.setName("Departement RH");
                rh.setType(TypeDepartement.RH);
                Departement newRH = departementRepository.save(rh);
                Long idRH = newRH.getId();



                User rrh = new User();
                rrh.setCin("22222222");
                rrh.setNomComplet("Maher Balti");
                rrh.setQualification("Responsable RH");
                rrh.setTelephone("22031146");
                rrh.setSexe(Sexe.MALE);
                rrh.setRole(Role.RRH);
                rrh.setUsername("22222222");
                rrh.setPassword(pwd.encode("22222222$mart"));
                rrh.setDepartement(newRH); // Departement RH
                rrh.setDoj(LocalDate.of(2020, 1, 1));
                rrh.setDob(LocalDate.of(2000, 1, 1));
                rrh.setEnabled(true);
                rrh.setIndependant(true);
                rrh.setDateCreation(LocalDateTime.now());

                User newRRH = userRepository.save(rrh);
                soldeService.addSolde(newRRH.getId());
                Departement xdRH = departementRepository.findById(idRH)
                        .orElseThrow(() -> new RuntimeException("Departement RH does not exist"));
                xdRH.setChefDepartement(newRRH);
                Departement xxxxxxrh = departementRepository.save(xdRH);
            }
        }

        return new ResponseEntity<String>("Ready to go AMIGOO!", HttpStatus.CREATED);
    }
}
