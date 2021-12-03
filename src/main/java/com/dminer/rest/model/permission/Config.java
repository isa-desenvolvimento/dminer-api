package com.dminer.rest.model.permission;

import com.dminer.dto.PermissionDTO;
import com.dminer.dto.PermissionReductDTO;

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
public class Config {
	private String descrConfig;
	private String token;
	
	public PermissionDTO toPermissionDTO() {
		return PermissionDTO
				.builder()
				.descrConfig(descrConfig)
				.token(token)
				.build();
	}
	
	public PermissionReductDTO toPermissionReductDTO() {
		return PermissionReductDTO
				.builder()
				.name(descrConfig)
				.id(token)
				.build();
	}
}
