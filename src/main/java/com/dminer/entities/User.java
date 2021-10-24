package com.dminer.entities;

import java.util.Date;

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
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class User {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

	@Column
	private String name; 
    
	@Column
	private Date dtBirthday;

	@ManyToOne
	private FileInfo avatar; 

	@ManyToOne
	private FileInfo banner; 

}
