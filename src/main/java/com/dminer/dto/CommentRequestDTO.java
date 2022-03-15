package com.dminer.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentRequestDTO {

    @NotBlank(message = "Login do usuário precisa estar preenchido")
    private String login;

    @NotNull(message = "Id do Post precisa estar preenchido")
    private Integer idPost;

    @DateTimeFormat(iso = ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "Data precisa estar preenchida")
    private String date;
    
    @NotBlank(message = "Conteúdo precisa estar preenchido")
    private String content;
}
