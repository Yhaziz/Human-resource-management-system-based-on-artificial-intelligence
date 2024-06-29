package com.smarttech.SmartRH.AppResouces.Repository;

import com.smarttech.SmartRH.AppResouces.Models.Entities.Notification;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByOrigine(User user);
    List<Notification> findByDestinationContaining(User user);
    List<Notification> findByVueParContaining(User user);
}
