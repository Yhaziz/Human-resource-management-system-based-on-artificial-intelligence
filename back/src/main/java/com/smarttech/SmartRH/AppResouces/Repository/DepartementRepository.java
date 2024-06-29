package com.smarttech.SmartRH.AppResouces.Repository;

import com.smarttech.SmartRH.AppResouces.Models.Entities.Departement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartementRepository extends JpaRepository<Departement, Long> {
    public Departement findByName(String deptName);
}