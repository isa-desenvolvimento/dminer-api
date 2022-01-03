package com.dminer.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    
    private Integer id;
    private UserReductDTO user;
    private List<String> likes = new ArrayList<>();
    private String content;
    private String title;
    private String type;
    private List<CommentDTO> comments = new ArrayList<>();
    private String anexo;


    
}
