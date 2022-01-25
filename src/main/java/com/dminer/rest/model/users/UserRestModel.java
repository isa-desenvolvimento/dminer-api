package com.dminer.rest.model.users;

import java.util.ArrayList;
import java.util.List;

import com.dminer.dto.UserReductDTO;

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
    private Output output = new Output();
    
    public boolean hasError() {
    	if (output == null)
    		return false;
    	return !output.getMessages().isEmpty(); 
    }

    public boolean isEmptyUsers() {
        return output.getResult().getUsuarios() == null && output.getResult().getUsuarios().isEmpty();
    }

    public List<Usuario> getUsers() {
        if (isEmptyUsers()) return new ArrayList<>();
        return output.getResult().getUsuarios();
    }

    public List<UserReductDTO> toUserReductDtoList() {
        List<UserReductDTO> reducts = new ArrayList<>();
        List<Usuario> users = getUsers();
        for (Usuario user : users) {
            reducts.add(user.toUserReductDTO());
        }
        return reducts;
    }

}