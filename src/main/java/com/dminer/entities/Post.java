package com.dminer.entities;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.dminer.enums.PostType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "POST"
	//, uniqueConstraints = @UniqueConstraint(columnNames={"likes"})
)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@ToString
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

	@Column
	private String title;
	
	@Column
	private String content; 

	@OneToMany(fetch = FetchType.EAGER)
	private List<Favorites> favorites;
	
	// @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	// private List<Like> likes = new ArrayList<>();
	
    @Column(length = 9999999)
    private String anexo;
    
	@Column
	private String login;
    
	@Column
	private Timestamp createDate = Timestamp.from(Instant.now());
	
	
	@Column
	@Enumerated(EnumType.STRING)
	private PostType type;	
	
	public Post(Integer id) { this.id = id; }

	public Post(String content) { this.content = content; }

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
}
