package com.dminer.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dminer.dto.PostDTO;
import com.dminer.dto.UserDTO;
import com.dminer.entities.User;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.PermissionRepository;
import com.dminer.repository.UserRepository;
import com.dminer.response.Response;
import com.dminer.services.interfaces.IUserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.ToString;

@Service
public class UserService implements IUserService {

    @Autowired
	private UserRepository userRepository;
    
    @Autowired
	private PermissionRepository permissionRepository;
	
    @Autowired
	private GenericRepositorySqlServer genericRepositorySqlServer;

    @Autowired
	private GenericRepositoryPostgres genericRepositoryPostgres;


	private static final Logger log = LoggerFactory.getLogger(UserService.class);


    @Override
    public User persist(User user) {
        log.info("Persistindo usuário: {}", user);
		return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(int id) {
        log.info("Buscando um usuário pelo id {}", id);
		return userRepository.findById(id);
    }

    @Override
    public Optional<List<User>> findAll() {
        log.info("Buscando todos os usuários");
		return Optional.ofNullable(userRepository.findAll());
    }

    @Override
    public void delete(int id) throws EmptyResultDataAccessException {
        log.info("Excluindo um usuário pelo id {}", id);
		userRepository.deleteById(id);
    }
    
    public List<UserDTO> search(String termo, String token) {    	
//    	Response<List<UserDTO>> users = carregarUsuariosApi(token);
    	List<UserDTO> pesquisa = new ArrayList<UserDTO>();
//    	if (!users.getErrors().isEmpty() && users.getData().isEmpty()) {
//    		return pesquisa;
//    	}
//    	
//    	if (termo == null) {
//    		return users.getData();
//    	}
    	
//    	termo = termo.toLowerCase();
//    	for (UserDTO u : users.getData()) {
//    		String concat = (u.getArea() + " " + u.getBirthDate() + " " + u.getEmail() + " " +
//    				u.getLinkedin() + " " + u.getLogin() + " " + u.getPermission()).toLowerCase();    		
//    		if (concat.contains(termo)) {
//    			pesquisa.add(u);
//    		}			
//		}
//    	
//    	if (pesquisa.isEmpty()) {
//    		return users.getData();
//    	}    	
    	return pesquisa;
    }
    
    
    public boolean existsByLogin(String login) {
        log.info("Verificando se usuário existe pelo login, {}", login);
        return userRepository.findByLogin(login) != null;
    }

    public Optional<User> findByLogin(String login) {
        log.info("Recuperando usuário pelo login, {}", login);
        return Optional.ofNullable(userRepository.findByLogin(login));
    }

    
    public String getToken() {
    	String uri = "https://www.dminerweb.com.br:8553/api/auth/login";
    	RestTemplate restTemplate = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();    	
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("userName", "matheus.ribeiro1");
        personJsonObject.put("userPassword", "#Matheus97");
        HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
        
        String personResultAsJsonStr = restTemplate.postForObject(uri, request, String.class);
        JSONObject retorno = new JSONObject(personResultAsJsonStr);
        return (String) retorno.get("baererAuthentication");
    }
    
    
    public Response<List<UserDTO>> carregarUsuariosApi2(String token) {
        log.info("Recuperando todos os usuário na api externa");

        String uri = "https://www.dminerweb.com.br:8553/api/administrative/client_area/user/select_user";
        List<UserDTO> usuarios = new ArrayList<>();        
    	RestTemplate restTemplate = new RestTemplate();
    	Response<List<UserDTO>> myresponse = new Response<>();
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("BAERER_AUTHENTICATION", token);
    	
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<String> entity = new HttpEntity<>("body", headers);
    	
    	ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);    	
    	if (response.toString().contains("O token informado é inválido") || response.toString().contains("expirou")) {
    		myresponse.getErrors().add(response.toString());
    		return myresponse;
    	}
    	
    	JSONObject personJsonObject = new JSONObject(response.getBody());    	
    	personJsonObject = (JSONObject) personJsonObject.get("output");
    	personJsonObject = (JSONObject) personJsonObject.get("result");    	
    	JSONArray arrayjs = personJsonObject.getJSONArray("usuarios");
    	arrayjs.forEach(el -> {
    		JSONObject jobj = (JSONObject) el;
    		String login = (String) jobj.get("login");
            String dtAniversario = (String) jobj.get("birthDate");
            String email = (String) jobj.get("email");
            String linkedin = (String) jobj.get("linkedinUrl");
            String area = (String) jobj.get("area");
//            byte[] avatar = getAvatar(login);
//            String encodedAvatar = "";
//            if (avatar != null)
//            	encodedAvatar = Base64.getEncoder().encodeToString(avatar);
            
            UserDTO user = new UserDTO();
            user.setBirthDate(dtAniversario);
            user.setLogin(login);
            user.setArea(area);
            user.setEmail(email);
            user.setLinkedin(linkedin);
//            user.setAvatar(encodedAvatar);
    		usuarios.add(user);
    	});
    	
    	myresponse.setData(usuarios);
    	return myresponse;
    }
    

    private Gson gson = new Gson();
    
    public void carregarUsuariosApi(String token) {
    	try {
    		
    		String uri = "https://www.dminerweb.com.br:8553/api/administrative/client_area/user/select_user";
    		URL url = new URL(uri);    		
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("BAERER_AUTHENTICATION", token);
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            
            String response = "";
            while (scanner.hasNext()) {
            	response += scanner.next();
            }
            
            if (response != null) {            	
            	System.out.println(response);
            	Output postRequestDTO = gson.fromJson(response, Output.class);
            	//System.out.println(postRequestDTO.result.usuarios.get(0).login);
            	//System.out.println(postRequestDTO.result.usuarios.get(0).dtAniversario);
            }
            scanner.close();
    	} catch (IOException e) {}
    	
    } 
    
    
    public byte[] getAvatar(String login) {
    	try {
    		BufferedImage image = ImageIO.read(new URL("https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=" + login));
    		if (image != null) {
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			ImageIO.write(image, "png", baos);
    			return baos.toByteArray();
    		}
    	} catch (IOException e) {}
    	return null;
    }    
}

@ToString
class Output {
    List<String> messages;
    Result result;
    
}

class Result {
	List<Usuario> usuarios;
}

class Usuario {
	String login;
	String token;
    String dtAniversario;
    String email;
    String linkedin;
    String area;
    byte[] avatar;
}
//
//"output": {
//    "messages": [],
//    "result": {
//        "usuarios": [
//            {
//                "token": "MW1rNnQ4ZDI5cjZ0N25sOGJoYmdndmtjYjU4",
//                "sunday": 0,
//                "wednesday": 1,
//                "thursday": 1,
//                "saturday": 0,
//                "monday": 1,
//                "friday": 1,
//                "tuesday": 1,
//                "document": "36933084892",
//                "email": "matheus.santos@dminer.com.br",
//                "endTime": "00:00",
//                "beginTime": "02:15",
//                "idStatus": 1,
//                "login": "DANILO.COLLADO",
//                "userName": "Matheus Ribeiro",
//                "administrator": 0,
//                "nameGroup": "GRUPO DCNH MANHA",
//                "birthDate": "23/08/1960",
//                "area": "ADMINISTRATIVO",
//                "linkedinUrl": "https://www.google.com/"
//            }