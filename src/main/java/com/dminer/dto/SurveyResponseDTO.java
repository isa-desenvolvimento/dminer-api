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
public class SurveyResponseDTO {
    private Integer id;
    private Integer idSurvey;
    private List<Integer> users;
    private Integer countA;
    private Integer countB;
}
