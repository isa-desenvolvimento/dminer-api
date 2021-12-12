package com.dminer.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.dminer.entities.Comment;
import com.dminer.entities.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    @Transactional(readOnly = true)
	Optional<List<Comment>> findByPost(Post post);

    //@Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Dispositivo t WHERE lower(mac) = :mac")
//    @Transactional(readOnly = true)
//    @Query("SELECT c FROM Comment c WHERE c.timestamp=:data")
//	List<Comment> findByTimestamp(@Param("data") Date timestamp);
}
