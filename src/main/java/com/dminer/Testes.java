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

import lombok.ToString;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public class Testes {

	public static void main2(String[] args) {
		
//		Gson gson = new Gson();
//		Container container = createOutputData();
//		try (FileWriter writer = new FileWriter("C:\\Users\\rhuan\\Desktop\\object-json.json")) {
//            gson.toJson(container, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
		
		
		
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Reader reader = new FileReader("C:\\Users\\rhuan\\Desktop\\example-data.json")) {		
        	Container staff = gson.fromJson(reader, Container.class);
        	System.out.println(staff);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	private static Container createOutputData() {
		Usuario usuario = new Usuario("rhuan.pablo", "123abc", "11/04/1995", "rhuan@email.com", "linkedin/rhuan", "TI");
		Result result = new Result();
		result.usuarios.add(usuario);
		Output output = new Output();
		output.result = result;
		Container container = new Container(output);
		return container;
	}
	
}

@ToString
class Container {
	Output output;
	public Container(Output output) {
		this.output = output;
	}
}

@ToString
class Output {
	public Output() {
		messages = new ArrayList<>();
	}
    List<String> messages;
    Result result;
    
}

@ToString
class Result {
	public Result() {
		usuarios = new ArrayList<>();
	}
	List<Usuario> usuarios;
}

@ToString
class Usuario {
	public Usuario(String login, String token, String birthDate, String email, String linkedinUrl, String area) {
		this.login = login;
		this.token = token;
		this.birthDate = birthDate;
		this.email = email;
		this.linkedinUrl = linkedinUrl;
		this.area = area;		
	}
	String login;
	String token;
    String birthDate;
    String email;
    String linkedinUrl;
    String area;
    byte[] avatar;
}
