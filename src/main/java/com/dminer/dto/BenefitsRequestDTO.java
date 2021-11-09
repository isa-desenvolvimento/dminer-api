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
public class BenefitsRequestDTO { 
    
    private int creator;
	private String title;
    private String content;
    private String date; //@Parameter(example = "01/01/2020")
    private String profiles;
    private String image;
}
