package com.dminer.dto;


//import io.swagger.v3.oas.annotations.Parameter;
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
public class TutorialsDTO { 
    
    private Integer id;
	private String title;
    private String content;
    private String date; 
    private Integer category;
    private Integer permission;
    private String image;
}
