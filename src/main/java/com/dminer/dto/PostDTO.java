package com.dminer.dto;

import java.util.ArrayList;
import java.util.List;

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
public class PostDTO { 
    
    private int id;
    private int idUsuario;
    private int likes;
    private String content;
    private String type;
    private List<CommentDTO> comments = new ArrayList<>();
    private List<String> anexos = new ArrayList<>();

}