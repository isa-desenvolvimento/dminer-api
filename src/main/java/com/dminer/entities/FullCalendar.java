package com.dminer.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
// @Table(name = "FULL_CALENDAR",
// uniqueConstraints={
//     @UniqueConstraint(columnNames = {"users"})
// })
@Table(name = "FULL_CALENDAR")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class FullCalendar {
 
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

    @Column
	private String title;

    @Column(name = "start_date")
    private Timestamp start;

    @Column(name = "end_date")
    private Timestamp end;

    @Column
    private Boolean allDay;

    @Column
    private String creator;

    @Column
    private String color;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<User> users = new ArrayList<>();

}
