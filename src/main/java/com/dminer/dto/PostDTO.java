package com.dminer.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Data
public class PostDTO { 
    
    @NotNull
    private Integer id;

    @NotNull
    private UserReductDTO user;

    @Builder.Default 
    private Map<String, List<String>> reacts = new HashMap<>();

    @NotBlank
    private String content;

    @NotBlank
    private String title;

    @NotBlank
    private String type;
    
    @Builder.Default 
    private List<CommentDTO> comments = new ArrayList<>();
    
    @Builder.Default 
    private List<String> favorites = new ArrayList<>();

    private String anexo;

    @DateTimeFormat(iso = ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
    private String dateCreated;

    public PostDTO(Integer id) {
        this.id = id;
    }
}
