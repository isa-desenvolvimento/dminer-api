package com.dminer.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "NOTICE")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Notice {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

    @Column
    private String creator;

    @OneToMany(fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    @Column
    private String warning;

    @Column
    private Timestamp date;

    @Column
    private Integer priority;

    @Column(columnDefinition = "default true")
    private Boolean active;
    
}
