package com.smarttech.SmartRH.AppResouces.Repository;

import com.smarttech.SmartRH.AppResouces.Models.Entities.Demande;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Token;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DemandeRepository extends JpaRepository<Demande, Long> {

    @Query("""
select t from Demande t inner join User u on t.user.id = u.id
where t.user.id = :userId 
""")
    List<Demande> findAllDemandesByUser(Long userId);
}
