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
public class UserRestModel<T> {
    private Output<T> output = new Output<T>();
    
    public boolean hasError() {
    	if (output == null)
    		return false;
    	return !output.getMessages().isEmpty(); 
    }

    public boolean isEmptyUsuarios() {
        return this == null || (output.getResult().getUsuarios() == null && output.getResult().getUsuarios().isEmpty());
    }

    public List<T> getUsuarios() {
        if (isEmptyUsuarios()) {
            return new ArrayList<>();
        }
        return output.getResult().getUsuarios();
    }

    public boolean isEmptyUsersAvatar() {
        return this == null || (output.getResult().getUsers() == null && output.getResult().getUsers().isEmpty());
    }

    /**
     * Recupera os usuários de avatar
     * @return
     */
    public List<T> getUsersAvatar() {
        if (isEmptyUsersAvatar()) {
            return new ArrayList<>();
        }
        return output.getResult().getUsers();
    }

    public List<UserReductDTO> fromUsuarioToUserReductDtoList() {
        List<UserReductDTO> reducts = new ArrayList<>();
        List<Usuario> users = (List<Usuario>) getUsuarios();
        for (Usuario user : users) {
            reducts.add(user.toUserReductDTO(true));
        }
        return reducts;
    }

}