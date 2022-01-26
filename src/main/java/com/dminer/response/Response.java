package com.dminer.response;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

public class Response<T> {

	private static final Logger log = LoggerFactory.getLogger(Response.class);

	private T data;
	private List<String> errors;

	public Response() {
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<String> getErrors() {
		if (this.errors == null) {
			this.errors = new ArrayList<String>();
		}
		return errors;
	}

	public boolean containErrors() {
		return getErrors().isEmpty();
	}

	public void setErrors(List<String> errors) {
		errors.forEach(err -> {
			addError(err);
		});
	}
	
	public void addError(String error) {
		log.info(error);
		getErrors().add(error);
	}

	public void addErrors(BindingResult result) {
		result.getAllErrors().forEach( e -> getErrors().add(e.getDefaultMessage()));
	}

}
