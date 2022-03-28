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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.dminer.dto.CommentDTO;
import com.dminer.dto.PostDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.enums.PostType;
import com.dminer.utils.UtilDataHora;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

	@Column
	private String title;
	
	@Lob
    @Column()
	private String content; 

    @Lob
    @Column()
    private String anexo;
    
	@Column
	private String login;
    
	@Column
	@Builder.Default
	private Timestamp createDate = Timestamp.from(Instant.now());
	
	
	@Column
	@Enumerated(EnumType.STRING)
	private PostType type;	
	
	public Post(Integer id) { this.id = id; }

	public Post(String content) { this.content = content; }



	public PostDTO convertDto() {
		return PostDTO
		.builder()
		.id(id)
		.content(content)
		.anexo(anexo)
		.title(title)
		.comments(new ArrayList<CommentDTO>())
		.user(new UserReductDTO(login))
		.type(type.name())
		.dateCreated(UtilDataHora.dateToFullStringUTC(createDate))
		.build();
	}



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
