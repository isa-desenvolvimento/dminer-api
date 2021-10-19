package com.dminer.dminer.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.dminer.dminer.entities.abstracts.Archive;

import lombok.ToString;

@Entity
@Table(name = "PHOTO")
@ToString(callSuper = true)
public class Photo extends Archive {
	
}
