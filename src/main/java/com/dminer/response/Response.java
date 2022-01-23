package com.dminer.response;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response<T> {

	private T data;
	private List<String> errors;
	private final Logger log = LoggerFactory.getLogger(Response.class);

	
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

	public void addError(String error) {
		log.error(error);
		getErrors().add(error);
	}

	public void setErrors(List<String> errors) {
		for (String error : errors) {
			addError(error);
		}
	}

}
