package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Notification;
import com.dminer.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    @Query("SELECT n FROM Notification n WHERE LOWER(n.notification) LIKE LOWER('%?1%') OR LOWER(n.user.login) LIKE LOWER('%?1%') ORDER BY n.createDate DESC")
    public List<Notification> search(String keyword);

    public List<Notification> findByUserOrAllUsersOrderByCreateDateDesc(User user, boolean allUsers);
}
