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
    
    private Integer id;
    private String login;
    private Integer likes;
    private String content;
    private String title;
    private String type;
    //private List<CommentDTO> comments = new ArrayList<>();
    private List<String> anexos = new ArrayList<>();

}
