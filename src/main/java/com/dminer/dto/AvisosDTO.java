package com.dminer.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class AvisosDTO {
    
    private Integer id;
    private List<Integer> usuarios = new ArrayList<>();
    private String data;
    private String criador;
    private String aviso;
    private String prioridade;

}
