package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.CommentDTO;
import com.dminer.dto.CommentRequestDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.Comment;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.services.PostService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentConverter implements Converter<Comment, CommentDTO, CommentRequestDTO>{

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private PostService postService;


    @Override
    public Comment dtoToEntity(CommentDTO commentDTO) {
        Comment c = new Comment();
        c.setId(commentDTO.getId());
        c.setContent(commentDTO.getContent());
        c.setTimestamp(UtilDataHora.toTimestamp(commentDTO.getDate()));

        Optional<User> user = userService.findByLogin(commentDTO.getUser().getLogin());
        if (user.isPresent()) {
            c.setUser(user.get());
        }
        
        Optional<Post> post = postService.findById(user.get().getId());
        if (post.isPresent()) {
            c.setPost(post.get());
        }
        return c;
    }
    
    @Override
    public CommentDTO entityToDto(Comment entity) {
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setDate(UtilDataHora.timestampToStringOrNow(entity.getTimestamp()));
      	dto.setIdPost(entity.getPost().getId());

        UserReductDTO user = userConverter.entityToUserReductDTO(entity.getUser());
        dto.setUser(user);
        return dto;
    }

    @Override
    public Comment dtoRequestToEntity(CommentRequestDTO requestDto) {
        Comment c = new Comment();
        c.setContent(requestDto.getContent() != null ? requestDto.getContent() : "");
        c.setTimestamp(UtilDataHora.toTimestamp(requestDto.getDate()));
        Optional<User> user = userService.findByLogin(requestDto.getLogin());
        if (user.isPresent()) {
            c.setUser(user.get());
        }
        Optional<Post> post = postService.findById(requestDto.getIdPost());
        if (post.isPresent()) {
            c.setPost(post.get());
        }
        return c;
    }
    
    public CommentDTO entityToDto(Integer idPost, UserReductDTO user, Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setContent(comment.getContent());
        dto.setDate(UtilDataHora.dateToFullStringUTC(comment.getTimestamp()));
        dto.setId(comment.getId());
        dto.setIdPost(idPost);
        dto.setUser(user);
        return dto;
    }

    public CommentDTO entityToDto(Integer idPost, Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setContent(comment.getContent());
        dto.setDate(UtilDataHora.dateToFullStringUTC(comment.getTimestamp()));
        dto.setId(comment.getId());
        dto.setIdPost(idPost);
        UserReductDTO user = userConverter.entityToUserReductDTO(comment.getUser());
        dto.setUser(user);
        return dto;
    }

    public Comment dtoToEntity(CommentDTO commentDTO, User user, Post post) {
        Comment c = new Comment();
        c.setId(commentDTO.getId());
        c.setContent(commentDTO.getContent());
        c.setTimestamp(UtilDataHora.toTimestamp(commentDTO.getDate()));
        c.setPost(post);
        c.setUser(user);
        return c;
    }


    


    public Comment requestDtoToEntity(CommentRequestDTO commentRequestDTO, User user, Post post) {
        Comment c = new Comment();
        c.setContent(commentRequestDTO.getContent());
        c.setTimestamp(UtilDataHora.toTimestamp(commentRequestDTO.getDate()));
        c.setUser(user);
        c.setPost(post);
        return c;
    }

    
    

}
