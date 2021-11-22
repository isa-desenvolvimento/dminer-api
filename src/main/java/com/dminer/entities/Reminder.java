package com.dminer.entities;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "REMINDER")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Reminder {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

    @ManyToOne
    private User user;

	@Column
	private String reminderDescrible; 

	@Column
    @Basic
    private Timestamp dataHora;

    // flag pra informar se o lembrete ainda está na data vigente
    @Column
    private boolean status; 


    
    public String getCron() {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        Date date = new Date(dataHora.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {  
            formatTimeStr = sdf.format(date);  
        }  
        return formatTimeStr;
    }  
    
}
