package com.dminer.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dminer.dto.UserDTO;
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
@EqualsAndHashCode(of = {"login"})
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

	@Lob
    @Column()
    // @Column(length = 9999999)
	private String banner; 

	@Lob
    @Column()
	private String avatar;
	

	public User (Integer id) {
		this.id = id;
	}

	public User (Integer id, String login) {
		this.id = id;
		this.login = login;
	}

	public User (String login, String userName) {
		this.login = login;
		this.userName = userName;
	}

	public UserReductDTO convertReductDto() {
		return UserReductDTO
		.builder()
		.login(login)
		.avatar(avatar)
		.userName(userName)
		.build();
	}

	public UserDTO convertDto() {
		return UserDTO
		.builder()
		.login(login)
		.avatar(avatar)
		.userName(userName)
		.banner(banner)
		.build();
	}

	@Override
	public String toString() {
		return "Usuário - id: " + id + ", username: " + userName + ", login: " + login + ", avatar: " + (avatar != null ? avatar.substring(0, 30) : "");
	}
}
