package com.dminer.rest.model.users;

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
public class UserRestModel {
    private Output output;
    
    public boolean hasError() {
    	if (output == null)
    		return false;
    	return !output.getMessages().isEmpty(); 
    }
}