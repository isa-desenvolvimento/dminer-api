package com.dminer.entities;


import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.dminer.enums.Profiles;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "BENEFITS")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Benefits {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;
    
	@Column
	private String title;

	@Column
	private String content;

    @OneToOne
    private User creator;

    @Enumerated(EnumType.STRING)
    private Profiles profiles;
    
    @Column
    private Timestamp date;

    @Column
    private String image;
}
