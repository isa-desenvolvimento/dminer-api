package com.dminer.dminer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dminer.dminer.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>{

}
