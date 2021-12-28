package com.dminer.entities;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dminer.services.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "NOTIFICATION")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Notification {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

    @ManyToOne
    private User user;

	@Column
	private String notification; 

	@Column
    private Boolean active = true;

    private Timestamp createDate = Timestamp.from(Instant.now()); 

	public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(this);
            System.out.println("ResultingJSONstring = " + json);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

	
	@Transient
    @Autowired
    private NotificationService notificationService;
    
    public void desactivate() {
        this.active = false;
        notificationService.persist(this);
    }
}
