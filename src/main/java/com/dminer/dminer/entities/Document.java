package com.dminer.dminer.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.dminer.dminer.entities.abstracts.Archive;

@Entity
@Table(name = "DOCUMENT")
public class Document extends Archive {
	
}
