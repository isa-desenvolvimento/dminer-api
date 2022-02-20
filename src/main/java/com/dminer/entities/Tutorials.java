package com.dminer.entities;


import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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

	@Column()
	private String content;

    // @OneToOne
    // @JoinColumn(name = "permission_id")
    // private Permission permission;

    private String permission;
    
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    private Timestamp date;

    @Column(length = 9999999)
    private String image;
}
