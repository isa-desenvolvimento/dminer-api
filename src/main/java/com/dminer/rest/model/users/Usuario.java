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
	private byte[] avatar;
	
	
	public User toUser() {
		return User
			.builder()
			.userName(userName)
			.login(login)
			.build();				
	}

	public UserDTO toUserDTO() {
		return UserDTO
				.builder()
				.userName(userName)
				.area(area)				
				.birthDate(birthDate)
				.email(email)
				.linkedinUrl(linkedinUrl)
				.login(login)
				.build();				
	}

	public UserReductDTO toUserReductDTO() {
		return UserReductDTO
				.builder()
				.userName(userName)
				.login(login)
				.build();				
	}

	
}
