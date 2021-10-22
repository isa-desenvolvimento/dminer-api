package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Comment;
import com.dminer.entities.Post;
import com.dminer.repository.CommentRepository;
import com.dminer.services.interfaces.ICommentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);


    @Override
    public Comment persist(Comment comment) {
        log.info("Persistindo um comentário {}", comment);
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(int id) {
        log.info("Buscando um comentário por id {}", id);
        return commentRepository.findById(id);
    }

    @Override
    public Optional<List<Comment>> findByPost(Post post) {
        log.info("Buscando um comentário pelo Post {}", post);
        return commentRepository.findByPost(post);
    }

    @Override
    public void delete(int id) {
        log.info("Deletando um comentário pelo id {}", id);
        commentRepository.deleteById(id);
    }
    
    
}
