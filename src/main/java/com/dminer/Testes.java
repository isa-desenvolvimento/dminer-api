package com.dminer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.dminer.dto.PostDTO;
import com.dminer.utils.UtilDataHora;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.google.gson.reflect.TypeToken;

import ch.qos.logback.classic.pattern.Util;

public class Testes {
	
	public static void main2(String[] args) {

		System.out.println(UtilDataHora.toTimestamp("2021-12-04 03:00:00"));
		String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(UtilDataHora.toTimestamp("2021-12-04 03:00:00"));
		System.out.println(s.substring(0, s.length() ));

		System.out.println("2021-12-04 03:00:00".equals("2021-12-04 03:00:01"));

		Timestamp t1 = UtilDataHora.toTimestamp("2021-12-04 03:00:00");
		Timestamp t2 = UtilDataHora.toTimestamp("2021-12-04 03:00:00");

		System.out.println(UtilDataHora.equals(t1, "2021-12-04 03:00:00"));

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
