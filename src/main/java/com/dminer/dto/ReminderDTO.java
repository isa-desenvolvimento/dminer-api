package com.dminer.dto;

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
public class ReminderDTO {
    
    private Integer id;

    private Integer idUser;

	private String reminderDescrible; 

    private String dataHora;

    // flag pra informar se o lembrete ainda está na data vigente
    private boolean status; 
}
