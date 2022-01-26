package com.dminer.controllers;

import java.util.Optional;

import javax.validation.Valid;
import javax.ws.rs.POST;

import com.dminer.converters.CommentConverter;
import com.dminer.dto.CommentDTO;
import com.dminer.dto.CommentRequestDTO;
import com.dminer.entities.Comment;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.services.CommentService;
import com.dminer.services.PostService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommentController {
    

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentConverter commentConverter;

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);


    private void validateRequestDto(CommentRequestDTO dto, BindingResult result) {
        if (dto.getContent() == null || dto.getContent().isEmpty())  {
            result.addError(new ObjectError("dto", "Conteúdo precisa estar preenchido."));			
		}
        if (dto.getDate() == null || !UtilDataHora.isTimestampValid(dto.getDate()))  {            
            result.addError(new ObjectError("dto", "Data precisa estar preenchido e ser válida."));
		}
        if (dto.getIdPost() == null)  {
            result.addError(new ObjectError("dto", "Id do Post precisa estar preenchido."));
		} else {
			Optional<Post> opt = postService.findById(dto.getIdPost()); 
            if (!opt.isPresent()) {
                result.addError(new ObjectError("dto", "Post não encontrado."));
            }
        }

        if (dto.getLogin() == null) {
            result.addError(new ObjectError("dto", "Id do usuário precisa estar preenchido."));
		} else {
		    String login = dto.getLogin();
		    if (!userService.existsByLogin(login)) {
		        result.addError(new ObjectError("dto", "Usuário: " + login + " não encontrado."));
		    }
		} 
    }


    @PostMapping()
    public ResponseEntity<Response<CommentDTO>> create(@Valid @RequestBody CommentRequestDTO dto, BindingResult result) {

		log.info("Salvando um novo comentário {}", dto.toString());

        Response<CommentDTO> response = new Response<>();

        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.addError(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Comment comment = commentService.persist(commentConverter.requestDtoToEntity(dto));
        response.setData(commentConverter.entityToDTO(comment));
        return ResponseEntity.ok().body(response);
    }
}
