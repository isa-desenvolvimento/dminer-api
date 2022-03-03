package com.dminer.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.dminer.rest.model.users.UserAvatar;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public final class DminerWebServiceBackup {    

    private static final Logger log = LoggerFactory.getLogger(DminerWebServiceBackup.class);
	
	private static DminerWebServiceBackup INSTANCE = null;
    private static UserRestModel<Usuario> usuarios ;
    private static UserRestModel<UserAvatar> avatares ;

	private static boolean callUsers;
	private static boolean callAvatares;


	private DminerWebServiceBackup() {}




	public static DminerWebServiceBackup getInstance() {
        if(INSTANCE == null) {
			log.info("Criando nova instancia de DminerWebServiceBackup");
            INSTANCE = new DminerWebServiceBackup();
			usuarios = null;
			avatares = null;
			callUsers = false;
			callAvatares = false;
        }
        return INSTANCE;
    }


	public UserRestModel<Usuario> getUsuariosApi(String token) {
		carregarAvatares(token);
		carregarUsuarios(token);
		associateAvatarWithUser();
		return usuarios;
	}


    // @Async
    private void carregarUsuarios(String token) {
    	
		log.info("Chamou o carregarUsuarios");

		if (token == null) {
			return;
		}
		
		
		if (callUsers) {
			log.info("Usuários já foram carregados");
			return;
		}
		
		callUsers = true;
		
		log.info("Recuperando todos os usuários na api externa");
		log.info(token.substring(0, 20) + "..." + token.substring(token.length()-20, token.length()));

    	String uri = "https://www.dminerweb.com.br:8553/api/administrative/client_area/user/select_user";		
		try {
			URL url = new URL(uri);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("BAERER_AUTHENTICATION", token);
			InputStream stream = connection.getInputStream();
			Scanner scanner = new Scanner(stream);
			
			String response = "";
			while (scanner.hasNext()) {
				response += scanner.next();
			}
			scanner.close();
			stream.close();
			connection.disconnect();
			
            if (retornoTokenInvalidoApi(response)) {
				log.info("retornoTokenInvalidoApi: {}", response);
				usuarios.getOutput().setMessages(Arrays.asList("Token expirado!", "Precisa fazer o login no sistema", token));
				return;
			}
			
			if (retornoInvalidoApi(response)) {
				log.info("retornoInvalidoApi: {}", response);
				usuarios.getOutput().setMessages(Arrays.asList("Verifique se você tem autorização adequada", "Entre em contato com o suporte", token));
				return;
			}
        
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				usuarios = gson.fromJson(response, new TypeToken<UserRestModel<Usuario>>(){}.getType());
                log.info("{} avatares carregados", usuarios.getUsuarios().size());
			} catch (IllegalStateException e) {
				return;
			}
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
        return;
    }


    // @Async
    private void carregarAvatares(String token) {
		
		log.info("Chamou o carregarAvatares");

		if (token == null) {
			return;
		}

		
		if (callAvatares) {
			log.info("Avatares já foram carregados");
			return;
		}

		callAvatares = true;

		log.info("Recuperando todos os avatares na api externa");
		log.info(token.substring(0, 20) + "..." + token.substring(token.length()-20, token.length()));

    	String uri = "https://www.dminerweb.com.br:8553/api/auth/all_avatar_customer";		
		try {
			URL url = new URL(uri);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("BAERER_AUTHENTICATION", token);
			InputStream stream = connection.getInputStream();
			Scanner scanner = new Scanner(stream);
			
			String response = "";
			while (scanner.hasNext()) {
				response += scanner.next();
			}
			scanner.close();
			stream.close();
			connection.disconnect();

			UserRestModel<UserAvatar> avatares = new UserRestModel<UserAvatar>();
			if (retornoTokenInvalidoApi(response)) {
				log.info("retornoTokenInvalidoApi: {}", response);
				avatares.getOutput().setMessages(Arrays.asList("Token expirado!", "Precisa fazer o login no sistema", token));
				return;
			}
			
			if (retornoInvalidoApi(response)) {
				log.info("retornoInvalidoApi: {}", response);
				avatares.getOutput().setMessages(Arrays.asList("Verifique se você tem autorização adequada", "Entre em contato com o suporte", token));
				return;
			}

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				avatares = gson.fromJson(response, new TypeToken<UserRestModel<UserAvatar>>(){}.getType());
                log.info("{} avatares carregados", avatares.getUsuarios().size());
				return;
			} catch (IllegalStateException e) {
				return;
			}
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        // return avatares;
	}


	public Usuario findUsuarioByUsername(String userName) {
		if (usuarios == null || usuarios.hasError() || usuarios.isEmptyUsuarios()) return null;

		return usuarios.getUsuarios().parallelStream().filter(usuario -> 
			usuario.getUserName().equals(userName)
		).findFirst().orElse(null);
	}


	public Usuario findUsuarioByLogin(String login) {
		if (usuarios == null || usuarios.hasError() || usuarios.isEmptyUsuarios()) return null;

		return usuarios.getUsuarios().parallelStream().filter(usuario -> 
			usuario.getLogin().equals(login)
		).findFirst().orElse(null);
	}

	public String getAvatarByUsername(String userName) {
		if (usuarios == null || usuarios.hasError() || usuarios.isEmptyUsuarios()) return null;

		Usuario userAvatar = usuarios.getUsuarios().parallelStream().filter(usuario -> 
			usuario.getUserName().equals(userName)
		).findFirst().orElse(null);

		if (userAvatar == null ) {
			return "data:image/png;base64," + usuarios.getOutput().getResult().getCommonAvatar();
		}
		return "data:image/png;base64," + userAvatar.getAvatar();
	}


	private void associateAvatarWithUser() {

		if (usuarios == null || avatares == null) {
			log.info("Nenhum dado carregado para rearranjar! Quantidade de usuários: {}, quantidade de avatares: {}", 0, 0);
			return;
		}


		log.info("Rearranjando {} usuários e {} avatares", usuarios.getUsuarios().size(), avatares.getUsuarios().size());

		usuarios.getUsuarios().parallelStream().forEach(usuario -> {
			avatares.getUsuarios().parallelStream().forEach(avatar -> {
				if (avatar.getUserName().equals(usuario.getUserName())) {
					if (avatar.isCommonAvatar()) {
						usuario.setAvatar("data:image/png;base64," + avatares.getOutput().getResult().getCommonAvatar());
					} else {
						usuario.setAvatar("data:image/png;base64," + avatar.getAvatar());
					}
				}
			});
		});

		log.info("Rearranjo completo!");
	}


    private static boolean retornoInvalidoApi(String response) {

		response = response.toLowerCase();
		return (
			response.contains("expirou") || 
			response.contains("não fez login") || 
			response.contains("fezologinnosistema") || 
			response.contains("hum...") ||
			response.contains("usuário inativo") ||
			response.contains("não autorizada") || 
			response.contains("ocorreu um problema") ||
			response.contains("não conseguiu acesso") ||
			response.contains("permissão de uso nesse dia") ||
			response.contains("acessar o sistema nesse horário") ||
			response.contains("não pude encontrar esse usuário") ||
			response.contains("não é um usuário interno da dminer")
		);		
	}

	private static boolean retornoTokenInvalidoApi(String response) {

		response = response.toLowerCase();
		return (
			response.contains("expirou") || 
			response.contains("não fez login") || 
			response.contains("fezologinnosistema") || 
			response.contains("hum...")
		);		
	}
    
}