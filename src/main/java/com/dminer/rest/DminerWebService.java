package com.dminer.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import com.dminer.rest.model.users.UserAvatar;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class DminerWebService {    

    private static final Logger log = LoggerFactory.getLogger(DminerWebService.class);
	
	private static DminerWebService INSTANCE = null;
    private static UserRestModel<Usuario> usuarios ;
    private static UserRestModel<UserAvatar> avatares ;

	private static boolean callUsers;
	private static boolean callAvatares;
	private static boolean rearranjo;

	private DminerWebService() {}



	public static DminerWebService getInstance() {
        if(INSTANCE == null) {
			log.info("Criando nova instancia de DminerWebService");
            INSTANCE = new DminerWebService();
			usuarios = null;
			avatares = null;
			callUsers = false;
			callAvatares = false;
        }
        return INSTANCE;
    }


	
	public UserRestModel<Usuario> getUsuariosApi(String token) {
		
		try {
			log.info("getUsuariosApi");
			ExecutorService threadpool = Executors.newCachedThreadPool();
			Future<UserRestModel<Usuario>> futureTask = threadpool.submit(() -> carregarUsuarios(token));
			while (!futureTask.isDone()) {
				// System.out.println("FutureTask is not finished yet..."); 
			}
			log.info("resultUsuarios");
			UserRestModel<Usuario> result = futureTask.get();			
			threadpool.shutdown();


			ExecutorService threadpool2 = Executors.newCachedThreadPool();
			Future<UserRestModel<UserAvatar>> futureTask2 = threadpool2.submit(() -> carregarAvatares(token));
			while (!futureTask2.isDone()) {
				// System.out.println("FutureTask is not finished yet...");
			}
			log.info("resultUsuarios");
			UserRestModel<UserAvatar> result2 = futureTask2.get();
			threadpool2.shutdown();
			log.info("resultAvatares");



			ExecutorService threadpool3 = Executors.newCachedThreadPool();
			Future<Void> futureTask3 = threadpool3.submit(() -> associateAvatarWithUser());
			while (!futureTask3.isDone()) {
				// System.out.println("FutureTask is not finished yet...");
			}
			log.info("associateAvatarWithUser");
			threadpool3.shutdown();

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return usuarios;
	}


    private UserRestModel<Usuario> carregarUsuarios(String token) {
    	
		log.info("Chamou o carregarUsuarios");

		if (token == null) {
			return null;
		}
		
		if (callUsers) {
			return usuarios;
		}

		log.info("Recuperando todos os usuários na api externa com o token: {}", token.substring(0, 20) + "..." + token.substring(token.length()-20, token.length()));

    	String uriUsers = "https://www.dminerweb.com.br:8553/api/administrative/client_area/user/select_user";
		
		try {

			HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(uriUsers))
			.headers("BAERER_AUTHENTICATION", token)
			.GET()
			.build();

			HttpResponse<String> response = HttpClient.newHttpClient()
			.send(request, HttpResponse.BodyHandlers.ofString());

			usuarios = new UserRestModel<Usuario>();
			if (retornoTokenInvalidoApi(response) || retornoInvalidoApi(response)) {
				usuarios.getOutput().setMessages(Arrays.asList("Token expirado!", "Precisa fazer o login no sistema", token));
				return usuarios;
			}
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				usuarios = gson.fromJson(response.body(), new TypeToken<UserRestModel<Usuario>>(){}.getType());
                log.info("{} usuarios carregados", usuarios.getUsuarios().size());
				callUsers = true;
			} catch (IllegalStateException e) {
				log.error("Falha ao converter response de usuario");
			}
		
		} catch (URISyntaxException | IOException | InterruptedException e1) {
			e1.printStackTrace();
		} 
        return usuarios;
    }


    // @Async
    private UserRestModel<UserAvatar> carregarAvatares(String token) {
		
		log.info("Chamou o carregarAvatares");

		if (token == null) {
			return null;
		}

		if (callAvatares) {
			return avatares;
		}

		String uriAvatares =  "https://www.dminerweb.com.br:8553/api/auth/all_avatar_customer";
		
		log.info("Recuperando todos os avatares na api externa");
		log.info(token.substring(0, 20) + "..." + token.substring(token.length()-20, token.length()));

		try {
			HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(uriAvatares))
			.headers("BAERER_AUTHENTICATION", token)
			.GET()
			.build();

			HttpResponse<String> response = HttpClient.newHttpClient()
			.send(request, HttpResponse.BodyHandlers.ofString());

			avatares = new UserRestModel<UserAvatar>();
			if (retornoTokenInvalidoApi(response) || retornoInvalidoApi(response)) {
				avatares.getOutput().setMessages(Arrays.asList("Token expirado!", "Precisa fazer o login no sistema", token));
				return avatares;
			}
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				avatares = gson.fromJson(response.body(), new TypeToken<UserRestModel<UserAvatar>>(){}.getType());
                log.info("{} avatares carregados", avatares.getUsersAvatar().size());
				callAvatares = true;
			} catch (IllegalStateException e) {
				log.error("Falha ao converter response de avatares");
			}
			
		} catch (URISyntaxException | IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
		
        return avatares;
	}


	public Usuario findUsuarioByUsername(String userName) {
		if (usuarios == null || usuarios.hasError() || usuarios.isEmptyUsuarios()) return null;

		return usuarios.getUsuarios().parallelStream().filter(usuario -> 
			usuario.getUserName().equals(userName)
		).findFirst().orElse(null);
	}


	public Usuario findUsuarioByLogin(String login) {
		if (usuarios == null || usuarios.hasError() || usuarios.isEmptyUsuarios()) return new Usuario();

		return usuarios.getUsuarios().parallelStream().filter(usuario -> 
			usuario.getLogin().equals(login)
		).findFirst().orElse(new Usuario());
	}

	public String getAvatarByUsername(String userName) {
		if (usuarios == null || usuarios.hasError() || usuarios.isEmptyUsuarios()) return null;

		Usuario userAvatar = usuarios.getUsuarios().parallelStream().filter(usuario -> 
			usuario.getUserName().equals(userName)
		).findFirst().orElse(new Usuario());

		if (userAvatar == null ) {
			return "data:image/png;base64," + usuarios.getOutput().getResult().getCommonAvatar();
		}
		return "data:image/png;base64," + userAvatar.getAvatar();
	}


	private Void associateAvatarWithUser() {

		if (usuarios == null || avatares == null) {
			log.info("Nenhum dado carregado para rearranjar! Quantidade de usuários: {}, quantidade de avatares: {}", 0, 0);
			return null;
		}

		if (rearranjo == true) return null;

		log.info("Rearranjando {} usuários e {} avatares", usuarios.getUsuarios().size(), avatares.getUsersAvatar().size());

		for (Usuario usuario : usuarios.getUsuarios()) {

			for (UserAvatar avatar : avatares.getUsersAvatar()) {

				if (usuario.getLogin().equalsIgnoreCase(avatar.getUserName()) || usuario.getUserName().equalsIgnoreCase(avatar.getUserName())) {
					if (avatar.isCommonAvatar()) {
						usuario.setAvatar("data:image/png;base64," + avatares.getOutput().getResult().getCommonAvatar());
					} else {
						usuario.setAvatar("data:image/png;base64," + avatar.getAvatar());
					}
				}
			}
		}
		rearranjo = true;
		log.info("Rearranjo completo!");
		return null;
	}


    private static boolean retornoInvalidoApi(HttpResponse<String> httpResponse) {

		String response = httpResponse.body().toLowerCase();
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

	private static boolean retornoTokenInvalidoApi(HttpResponse<String> httpResponse) {

		String response = httpResponse.body().toLowerCase();
		return (
			response.contains("expirou") || 
			response.contains("não fez login") || 
			response.contains("fezologinnosistema") || 
			response.contains("hum...")
		);		
	}
    

	public boolean jaProcessouUsuarios() {
		return callAvatares && callUsers && rearranjo;
	}
}