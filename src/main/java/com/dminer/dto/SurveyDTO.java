package com.dminer.dto;

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
public class SurveyDTO {    
    private Integer id;
	private String question;
    private String optionA;
    private String optionB;
    private Integer countA = 0;
    private Integer countB = 0;
    private String date;
    private Boolean voted = false;
    private Boolean active;
}
