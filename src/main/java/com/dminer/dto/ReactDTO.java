package com.dminer.dto;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ReactDTO {
	// react, users
	private Map<String, List<String>> reacts = new HashMap<>();
}
