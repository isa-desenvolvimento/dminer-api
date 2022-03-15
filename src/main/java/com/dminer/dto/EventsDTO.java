package com.dminer.dto;

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
public class EventsDTO {

	private Integer id;
	private String title;
    private String startDate;
    private String endDate;
    private Boolean allDay;
    private String startRepeat;
    private String endRepeat;
    private String location;
    private String reminder;
    private String description;
}