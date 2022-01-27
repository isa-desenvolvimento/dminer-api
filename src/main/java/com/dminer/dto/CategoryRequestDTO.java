package com.dminer.dto;


import javax.validation.constraints.NotBlank;

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
public class CategoryRequestDTO {

	@NotBlank(message = "Titulo precisa estar preenchido") 
	private String title;
}
