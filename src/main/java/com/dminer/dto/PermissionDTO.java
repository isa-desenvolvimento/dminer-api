package com.dminer.dto;


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
@Builder
@ToString
public class PermissionDTO {
    private String token;
	private String descrConfig;
	
	public PermissionReductDTO toPermissionReductDTO() {
		return PermissionReductDTO
				.builder()
				.name(descrConfig)
				.id(token)
				.build();
	}
	
}
