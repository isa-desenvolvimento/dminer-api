package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
 
    
    List<Notice> findAllByOrderByDateDesc(); 
    
}
