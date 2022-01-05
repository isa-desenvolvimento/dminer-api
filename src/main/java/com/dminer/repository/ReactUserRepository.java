package com.dminer.repository;


import java.util.List;

import com.dminer.entities.Benefits;
import com.dminer.entities.ReactUser;
import com.dminer.entities.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactUserRepository extends JpaRepository<ReactUser, Integer> {
    
    Boolean existsByLoginAndPost(String login, Post post);

    List<ReactUser> findByPost(Post post);
}
