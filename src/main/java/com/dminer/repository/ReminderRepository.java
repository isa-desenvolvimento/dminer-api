package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Reminder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    
    @Query("SELECT r FROM Reminder r WHERE LOWER(r.reminderDescrible) LIKE LOWER('%?1%') AND r.user.login = '?2' ORDER BY r.date DESC")
    public List<Reminder> search(String keyword, String login);

    public List<Reminder> findAllByOrderByDateDesc();

}
