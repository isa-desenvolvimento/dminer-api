package com.dminer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import com.dminer.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    //login
    List<Post> findAllByLogin(String login);

    List<Post> findAllByCreateDate(Timestamp date);
}
