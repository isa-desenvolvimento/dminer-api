package com.dminer.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.dminer.dto.CommentDTO;
import com.dminer.utils.UtilDataHora;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "COMMENT")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
@Builder
public class Comment {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Integer id;

	@Column
	private String content;

    @ManyToOne
    private User user;

    @OneToOne
    private Post post;

    @Column
    private Timestamp timestamp;
    

    public CommentDTO convertDto() {
        return CommentDTO
        .builder()
        .id(id)
        .content(content)
        .date(UtilDataHora.dateToFullStringUTC(timestamp))
        .idPost(post.getId())
        .user(user.convertReductDto())        
        .build();
    }
}
