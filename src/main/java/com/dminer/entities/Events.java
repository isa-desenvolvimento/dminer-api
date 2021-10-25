package com.dminer.entities;

import java.util.Date;

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
    private Date startDate;

    @Column
    private Date endDate;

    @Column
    private Boolean allDay;

    @Enumerated(EnumType.STRING)
    @Column
    private EventsTime startRepeat;

    @Enumerated(EnumType.STRING)
    @Column
    private EventsTime endRepeat;

    @Column
    private String location;

    @Enumerated(EnumType.STRING)
    @Column
    private EventsTime reminder;

    @Column
    private String description;

}