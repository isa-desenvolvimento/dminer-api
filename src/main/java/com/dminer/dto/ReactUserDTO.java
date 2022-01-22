package com.dminer.dto;

import java.sql.Timestamp;

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
public class ReactUserDTO {
    
    private Integer id;
	private Timestamp createDate;
	private String login;
	private String react;
	private Integer post; 
}
