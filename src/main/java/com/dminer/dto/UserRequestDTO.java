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
public class UserRequestDTO {
    
    private String name;
    private String dtBirthday;
    private String avatar;
    private String banner;
    private String area;
	private String linkedin;
	private String email;
	private String profile;

}
