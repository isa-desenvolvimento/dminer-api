package com.dminer.repository;


import java.util.List;

import com.dminer.entities.Benefits;
import com.dminer.entities.Like;
import com.dminer.entities.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Like, Integer> {
    
    Boolean existsByLoginAndPost(String login, Post post);

    List<Like> findByPost(Post post);
}
