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
public class PostRequestDTO { 
    
    private Integer idUsuario;
    private Integer likes;
    private String content;
    private String type;
    private List<CommentRequestDTO> comments = new ArrayList<>();
}
