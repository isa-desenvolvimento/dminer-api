package com.dminer.entities;


import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dminer.enums.Category;
import com.dminer.enums.Profiles;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TUTORIALS")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Tutorials {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;
    
	@Column
	private String title;

	@Column(length = 9999999)
	private String content;

    @Enumerated(EnumType.STRING)
    private Profiles profile;
    
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column
    private Timestamp date;

    @Column
    private String image;
}
