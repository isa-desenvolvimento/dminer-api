package com.dminer.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private String login;
    private String userName;
    private String avatar;
    private String banner;
    private String birthDate;
    private String email;
    private String linkedin;
    private String area;
    private String permission;
    
    
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
        	return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
