package com.dminer.repository;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Comment;
import com.dminer.entities.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    @Transactional(readOnly = true)
	Optional<List<Comment>> findByPost(Post post);

}
