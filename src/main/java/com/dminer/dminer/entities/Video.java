package com.dminer.dminer.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.dminer.dminer.entities.abstracts.Archive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "VIDEO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Video extends Archive {

}
