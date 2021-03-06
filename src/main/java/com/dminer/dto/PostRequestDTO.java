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
    
	private String title;
    private String login;
    // private List<String> likes;
    private String content;
    private Integer type;
    private String anexo;
    //private List<CommentRequestDTO> comments = new ArrayList<>();
}
