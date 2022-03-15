package com.dminer.rest.model.users;

import java.util.ArrayList;
import java.util.List;

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
public class Result<T> {
	private String commonAvatar;
	private List<T> usuarios = new ArrayList<>();
	private List<T> users = new ArrayList<>();
}
