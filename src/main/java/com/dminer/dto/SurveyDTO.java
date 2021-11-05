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
public class SurveyDTO {    
    private Integer id;
    private Integer idUser;
	private String question;
    private String optionA;
    private String optionB;
    private String date;
}
