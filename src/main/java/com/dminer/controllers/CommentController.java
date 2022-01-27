package com.dminer.controllers;


import javax.validation.Valid;

import com.dminer.constantes.MessagesConst;
import com.dminer.converters.CommentConverter;
import com.dminer.dto.CommentDTO;
import com.dminer.dto.CommentRequestDTO;
import com.dminer.entities.Comment;
import com.dminer.response.Response;
import com.dminer.services.CommentService;
import com.dminer.validadores.Validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class CommentController {
    
    @Autowired
    private Validators validators;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentConverter commentConverter;

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);


    @PostMapping()
    public ResponseEntity<Response<CommentDTO>> create(@Valid @RequestBody CommentRequestDTO dto, BindingResult result) {

		log.info(MessagesConst.SALVANDO_REGISTRO, dto.toString());

        Response<CommentDTO> response = new Response<>();

        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            response.addErrors(result);
            return ResponseEntity.badRequest().body(response);
        }

        Comment comment = commentService.persist(commentConverter.dtoRequestToEntity(dto));
        response.setData(commentConverter.entityToDto(comment));
        return ResponseEntity.ok().body(response);
    }


    private void validateRequestDto(CommentRequestDTO dto, BindingResult result) {
        
        if (!validators.existsPostById(dto.getIdPost())) {
            result.addError(new ObjectError("dto", "Post não encontrado."));
        }

        String login = dto.getLogin();
        if (!validators.existsUserByLogin(login)) {
            result.addError(new ObjectError("dto", "Usuário: " + login + " não encontrado."));
        }
    }
}
