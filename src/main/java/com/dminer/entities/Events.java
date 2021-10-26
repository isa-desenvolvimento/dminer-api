package com.dminer.entities;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dminer.enums.EventsTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "EVENTS")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Events {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

    @Column
	private String title;

    @Column
    @Basic
    private Timestamp startDate;

    @Column
    @Basic
    private Timestamp endDate;

    @Column
    private Boolean allDay;

    @Column
    @Enumerated(EnumType.STRING)
    private EventsTime startRepeat;

    @Column
    @Enumerated(EnumType.STRING)
    private EventsTime endRepeat;

    @Column
    private String location;

    @Column
    @Enumerated(EnumType.STRING)
    private EventsTime reminder;

    @Column
    private String description;

}