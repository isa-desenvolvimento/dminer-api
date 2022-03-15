package com.dminer.repository;


import java.util.List;

import com.dminer.entities.Tutorials;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorialsRepository extends JpaRepository<Tutorials, Integer> {
    
    List<Tutorials> findAllByOrderByDateDesc();

    //findAllByOrderByCreateDateDesc
}
