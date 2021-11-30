package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    @Query("SELECT n FROM Notification n WHERE LOWER(n.notification) LIKE LOWER('%?1%')")
    public List<Notification> search(String keyword);
}
