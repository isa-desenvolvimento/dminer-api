package com.dminer.dto;

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
public class PermissionReductDTO {
	private String id;
	private String name;
	
	public PermissionDTO toPermissionDTO() {
		return PermissionDTO
				.builder()
				.descrConfig(name)
				.token(id)
				.build();
	}
}
