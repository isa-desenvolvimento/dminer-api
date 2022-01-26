package com.dminer.dto;


import javax.validation.constraints.NotBlank;

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
public class BenefitsRequestDTO { 
    
    @NotBlank(message = "Responsável precisa estar preenchido")
    private String creator;
    
    @NotBlank(message = "Titulo precisa estar preenchido")
	private String title;
    
    @NotBlank(message = "Conteúdo precisa estar preenchido")
    private String content;

    @DateTimeFormat(iso = ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "Data precisa estar preenchida")
    private String date;
    
    @NotBlank(message = "Permissão precisa estar preenchido")
    private Integer permission;

    private String image;

}
