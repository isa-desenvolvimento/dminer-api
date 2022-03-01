package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.CommentDTO;
import com.dminer.dto.CommentRequestDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.Comment;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.rest.model.users.UserAvatar;
import com.dminer.rest.model.users.UserRestModel;
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
    
    /**
     * Cuidado ao usar este método dentro de um foreach! Para cada comentário 
     * o avatar do usuário é buscado na api
     */
    @Override
    public CommentDTO entityToDto(Comment entity) {
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setDate(UtilDataHora.timestampToStringOrNow(entity.getTimestamp()));
      	dto.setIdPost(entity.getPost().getId());
        String avatar = userService.getAvatarEndpoint(entity.getUser().getLogin());
        UserReductDTO user = userConverter.entityToUserReductDTO(entity.getUser());
        user.setAvatar(avatar);
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


    /**
     * Tras o avatar do usuário do comentário, dado uma lista de avatares
     * @param entity
     * @param allAvatarCustomer
     * @return
     */
    public CommentDTO entityToDto(Comment entity, UserRestModel<UserAvatar> allAvatarCustomer) {
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setDate(UtilDataHora.timestampToStringOrNow(entity.getTimestamp()));
      	dto.setIdPost(entity.getPost().getId());
        String avatar = getAvatarByUsername(allAvatarCustomer, entity.getUser().getUserName());
        UserReductDTO user = userConverter.entityToUserReductDTO(entity.getUser(), avatar);
        dto.setUser(user);
        return dto;
    }

    private String getAvatarByUsername(UserRestModel<UserAvatar> usuarios, String userName) {
		UserAvatar userAvatar = usuarios.getUsers().stream().filter(usuario -> 
			usuario.getUserName().equals(userName)
		).findFirst().orElse(null);
		if (userAvatar == null || userAvatar.isCommonAvatar()) {
			return "data:image/png;base64," + usuarios.getOutput().getResult().getCommonAvatar();
		}
		return "data:image/png;base64," + userAvatar.getAvatar();
	}
}
