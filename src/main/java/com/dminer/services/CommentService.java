package com.dminer.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.dminer.entities.Comment;
import com.dminer.entities.Post;
import com.dminer.entities.User;
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
        log.info("Persistindo um comentário {}", comment.getContent());
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(int id) {
        log.info("Buscando um comentário por id {}", id);
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findByPost(Post post) {
        log.info("Buscando um comentário pelo Post {} - {}", post.getId(), post.getTitle());
        List<Comment> comments = commentRepository.findByPost(post);
        return comments;
    }
    
    @Override
    public void delete(int id) {
        log.info("Deletando um comentário pelo id {}", id);
        commentRepository.deleteById(id);
    }
    

    public List<Comment> searchCommentsByPostIdAndDateAndUser(Post post, Timestamp date, User user) {

        log.info("Buscando comentários por post / data / usuário {}, {}, {}", post.getId(), date, user.getId());
        
        List<Comment> comments = findByPost(post);
        if (comments == null) {
            return new ArrayList<Comment>();
        }

        comments.forEach(c -> {
            System.out.println(c.getUser().getId());
        });

        comments = comments.stream()
        .filter(comment -> date != null && comment.getTimestamp().equals(date))
        .filter(comment -> comment.getUser().getId().equals(user.getId()))
        .collect(Collectors.toList());
        return sort(comments);
        // return comments;
    }



    public List<Comment> sort(List<Comment> comments) {
        if (comments == null) {
            return new ArrayList<Comment>();
        }

        comments = comments.stream()
		.sorted(Comparator.comparing(Comment::getTimestamp).reversed())
		.collect(Collectors.toList());
        return comments;
    }
    
}
