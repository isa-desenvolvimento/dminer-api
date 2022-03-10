package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.CommentDTO;
import com.dminer.dto.CommentRequestDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.Comment;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.rest.DminerWebService;
import com.dminer.rest.model.users.UserAvatar;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
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

    // @Autowired
    // private DminerWebService dminerWebService;
    

    @Override
    public Comment dtoToEntity(CommentDTO commentDTO) {
        Comment c = new Comment();
        c.setId(commentDTO.getId());
        c.setContent(commentDTO.getContent());
        c.setTimestamp(UtilDataHora.toTimestamp(commentDTO.getDate()));

        User user = new User();
        Optional<User> userTemp = userService.findByLogin(commentDTO.getUser().getLogin());
        if (userTemp.isPresent()) {
            user = userTemp.get();
            c.setUser(user);
        }

        // Usuario usuario = DminerWebService.getInstance().findUsuarioByLogin(commentDTO.getUser().getLogin());
        // if (usuario != null) {
        //     user = usuario.toUser(true);
        //     c.setUser(user);
        // } else {
        //     Optional<User> userTemp = userService.findByLogin(commentDTO.getUser().getLogin());
        //     if (userTemp.isPresent()) {
        //         user = userTemp.get();
        //         c.setUser(user);
        //     }
        // }
        
        Optional<Post> post = postService.findById(user.getId());
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
        
        // Usuario usuario = DminerWebService.getInstance().findUsuarioByLogin(entity.getUser().getLogin());
        // if (usuario != null) {
        //     UserReductDTO user = usuario.toUserReductDTO(true);
        //     dto.setUser(user);
        // }
        dto.setUser(entity.getUser().convertReductDto());
        return dto;
    }

    @Override
    public Comment dtoRequestToEntity(CommentRequestDTO requestDto) {
        Comment c = new Comment();
        c.setContent(requestDto.getContent() != null ? requestDto.getContent() : "");
        c.setTimestamp(UtilDataHora.toTimestamp(requestDto.getDate()));
        User user = new User();

        Optional<User> userTemp = userService.findByLogin(requestDto.getLogin());
        if (userTemp.isPresent()) {
            user = userTemp.get();
            c.setUser(user);
        }

        // Usuario usuario = DminerWebService.getInstance().findUsuarioByLogin(requestDto.getLogin());
        // if (usuario != null) {
        //     user = usuario.toUser(true);
        //     c.setUser(user);
        // } else {
        //     Optional<User> userTemp = userService.findByLogin(requestDto.getLogin());
        //     if (userTemp.isPresent()) {
        //         user = userTemp.get();
        //         c.setUser(user);
        //     }
        // }
        Optional<Post> post = postService.findById(requestDto.getIdPost());
        if (post.isPresent()) {
            c.setPost(post.get());
        }
        return c;
    }
    

    /**
     * Não traz o avatar do usuário do comentário
     * @param idPost
     * @param comment
     * @return
     */
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
