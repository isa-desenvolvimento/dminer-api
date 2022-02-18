package com.dminer.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dminer.dto.SurveyDTO;
import com.dminer.utils.UtilDataHora;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "SURVEY")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
@Builder
public class Survey {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

    @Column
    private String question;

    @Column
    private String optionA;

    @Column
    private String optionB;

    @Column
    private Timestamp date;

    @Column
    private Boolean active = true;


    public SurveyDTO convertDto() { 
        SurveyDTO dto = new SurveyDTO();
        dto.setId(id);
        dto.setDate(date != null ? UtilDataHora.timestampToStringOrNow(date) : null);
        dto.setOptionA(optionA);
        dto.setOptionB(optionB);
        dto.setQuestion(question);        
        dto.setCountA(0);
        dto.setCountB(0);
        dto.setActive(active);
        return dto;
    }

    public SurveyDTO convertDto(SurveyResponses surveyResponses) { 
        SurveyDTO dto = new SurveyDTO();
        dto.setId(id);
        dto.setDate(date != null ? UtilDataHora.timestampToStringOrNow(date) : null);
        dto.setOptionA(optionA);
        dto.setOptionB(optionB);
        dto.setQuestion(question);        
        dto.setCountA(surveyResponses.getCountA());
        dto.setCountB(surveyResponses.getCountB());
        dto.setActive(active);
        return dto;
    }
}
