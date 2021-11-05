package com.dminer.entities;

import java.sql.Timestamp;

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
@Table(name = "SURVEY")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
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

    // @ManyToOne
    // private User user;

    @Column
    private Timestamp date;

}
