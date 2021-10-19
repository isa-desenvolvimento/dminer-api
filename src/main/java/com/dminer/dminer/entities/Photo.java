package com.dminer.dminer.entities;

import com.dminer.dminer.entities.abstracts.Archive;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PHOTO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Photo extends Archive {

}
