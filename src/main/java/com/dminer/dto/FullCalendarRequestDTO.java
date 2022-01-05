package com.dminer.dto;

import java.util.ArrayList;
import java.util.List;

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
public class FullCalendarRequestDTO {
	private String title;
    private String start;
    private String end;
    private Boolean allDay;
    private String creator;
    private String color;
    private List<String> users = new ArrayList<>();
}