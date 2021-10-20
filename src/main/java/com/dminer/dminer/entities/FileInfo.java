package com.dminer.dminer.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "FILE_INFO")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class FileInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Exclude
	private Integer id;

	@Column
	private String url;
	
	@JsonIgnore
	@ManyToOne()
	@JoinColumn(name = "post_id")
	private Post post;
	

	
	public FileInfo(String url) {
		this.url = url;
	}

    public FileInfo(String url, Post post) {
		this.url = url;
		this.post = post;
    }
}
