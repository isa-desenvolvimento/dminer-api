package com.dminer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.dminer.dto.PostDTO;
import com.dminer.utils.UtilDataHora;
import com.dminer.utils.UtilFilesStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public class Testes {



	public static void main3(String[] args) {
		String file = UtilFilesStorage.loadFile("c:\\projeto-java-andressa\\dminer-api\\files\\rhuanpablo.pdf");
        System.out.println(file);	
	}

	public static void main2(String[] args) {

		List<Pessoa> pessoas = new ArrayList<>();
		Pessoa p1 = new Pessoa(1, "Rhuan");
		Pessoa p2 = new Pessoa(2, "Pablo");
		Pessoa p3 = new Pessoa(3, "Cesario");

		pessoas.add(p1);
		pessoas.add(p2);
		pessoas.add(p3);

		pessoas.forEach(p -> {
			if (pessoas.contains(new Pessoa(4, "Cesario 222"))) {
				System.out.println("Existe");
			}
		});
	}
		
}

@EqualsAndHashCode(of = {"id"})
class Pessoa {
	int id;
	String nome;

	public Pessoa(int id, String nome) {
		this.id = id;
		this.nome = nome;
	}
}
