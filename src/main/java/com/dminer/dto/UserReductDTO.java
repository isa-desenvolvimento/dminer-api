package com.dminer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class UserReductDTO {
	private String login;
    private String userName;
    private String avatar;
    
    public UserReductDTO(String login) {
    	this.login = login;
    }
}
