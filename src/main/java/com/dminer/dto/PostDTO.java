package com.dminer.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PostDTO { 
    
    private Integer id;
    private UserReductDTO user;
    private Map<String, List<String>> reacts = new HashMap<>();
    private String content;
    private String title;
    private String type;
    private List<CommentDTO> comments = new ArrayList<>();
    private String anexo;

    public PostDTO(Integer id) {
        this.id = id;
    }
    
}
