package com.dminer.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dminer.dto.UserReductDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
@Builder
public class User {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

	@Column(unique = true)
	private String login;
    
	@Column()
	private String userName;

	@Column(length = 8000)
	private String banner; 

	@Transient
	private String avatar;
	
	public User (String login, String userName) {
		this.login = login;
		this.userName = userName;
	}

	public UserReductDTO convertReductDto() {
		return UserReductDTO
		.builder()
		.login(login)
		// .avatar(avatar)
		.userName(userName)
		.build();
	}
}
