package com.dminer.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PostRequestDTO { 
    
    @NotBlank(message = "Titulo precisa estar preenchido")
	private String title;

    @NotBlank(message = "Login precisa estar preenchido")
    private String login;
    
    @NotBlank(message = "Conteúdo precisa estar preenchido")
    private String content;

    @NotBlank(message = "Tipo informado precisa ser 1 para Interno ou 2 para Externo")
    private Integer type;

    private String anexo;
}
