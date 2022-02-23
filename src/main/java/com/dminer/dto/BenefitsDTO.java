package com.dminer.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//import io.swagger.v3.oas.annotations.Parameter;
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
public class BenefitsDTO {
    
    @NotNull
    private Integer id;
    
    @NotBlank
    private String creator;
	
    private String title;
    private String content;
    private String date; 

    @NotNull
    private Integer permission;
    
    private String image;
}
