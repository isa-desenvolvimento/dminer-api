package com.dminer.rest.model.permission;

import com.dminer.rest.model.permission.Output;

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
public class ConfigRestModel {
	private Output output;
	
	public boolean hasError() {
    	if (output == null)
    		return false;
    	return !output.getMessages().isEmpty(); 
    }
}
