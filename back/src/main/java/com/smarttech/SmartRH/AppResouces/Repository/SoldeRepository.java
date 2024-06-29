package com.smarttech.SmartRH.AppResouces.Repository;

import com.smarttech.SmartRH.AppResouces.Models.Entities.Solde;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SoldeRepository extends JpaRepository<Solde, Long> {
    Optional<Solde> findByUser(User user);
}
