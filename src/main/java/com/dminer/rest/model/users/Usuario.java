package com.dminer.rest.model.users;

import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.User;

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
public class Usuario {
	private String login;
	private String userName;
	private String token;
	private String birthDate;
	private String email;
	private String linkedinUrl;
	private String area;
	private String avatar;
	
	
	public User toUser(boolean avatar) {
		return User
			.builder()
			.userName(userName)
			.login(login)
			.avatar(avatar == true ? this.avatar : null)
			.build();				
	}

	public UserDTO toUserDTO(boolean avatar) {
		return UserDTO
			.builder()
			.userName(userName)
			.area(area)				
			.birthDate(birthDate)
			.email(email)
			.linkedinUrl(linkedinUrl)
			.login(login)
			.avatar(avatar == true ? this.avatar : null)
			.build();				
	}

	public UserReductDTO toUserReductDTO(boolean avatar) {
		return UserReductDTO
			.builder()
			.userName(userName)
			.login(login)
			.avatar(avatar == true ? this.avatar : null)
			.build();				
	}

	
}
