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
public class UserDTO {
    
    private Integer id;
    private String name;
    private String dtBirthday;
    private byte[] avatar;
    private byte[] banner; 
    private String area;
	private String linkedin;
	private String email;
	private String profile;
}
