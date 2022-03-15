package com.dminer.repository;

import com.dminer.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByLogin(@Param("login") String login);

    public Boolean existsByLogin(@Param("login") String login);
    
    public User findByLoginAndUserName(@Param("login") String login, @Param("userName") String userName);
}
