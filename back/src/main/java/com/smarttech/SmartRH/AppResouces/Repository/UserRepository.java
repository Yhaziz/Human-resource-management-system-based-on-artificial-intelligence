package com.smarttech.SmartRH.AppResouces.Repository;

import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByTelephone(String telephone);
    Boolean existsByCin(String cin);
}
