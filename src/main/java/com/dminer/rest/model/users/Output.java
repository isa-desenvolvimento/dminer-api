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
public class Output<T> {
	private List<String> messages = new ArrayList<>();
	
	private Result<T> result = new Result<T>();
}