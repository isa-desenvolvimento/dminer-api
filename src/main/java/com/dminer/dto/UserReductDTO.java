package com.dminer.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class UserReductDTO {
	private String login;
    private String username;
    private String avatar;
    
    public UserReductDTO(String login) {
    	this.login = login;
    }
}
