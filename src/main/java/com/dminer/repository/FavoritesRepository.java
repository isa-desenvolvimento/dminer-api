package com.dminer.repository;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Favorites;
import com.dminer.entities.Post;
import com.dminer.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {
    
    List<Favorites> findAllByUser(User user);

    List<Favorites> findAllByPost(Post post);

    Optional<Favorites> findByUserAndPost(User user, Post post);
}
