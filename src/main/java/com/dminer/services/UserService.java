package com.dminer.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dminer.components.TokenService;
import com.dminer.controllers.Token;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.User;
import com.dminer.images.ImageResizer;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.PermissionRepository;
import com.dminer.repository.UserRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
import com.dminer.services.interfaces.IUserService;
import com.dminer.utils.UtilDataHora;
import com.dminer.utils.UtilFilesStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class UserService implements IUserService {

    @Autowired
	private UserRepository userRepository;
    
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private UserRestModel userRestModel = new UserRestModel();


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

		List<UserDTO> pesquisa = new ArrayList<UserDTO>();

		// se vier null ou conter erros, retorna lista vazia
		userRestModel = carregarUsuariosApi(token);
		if (userRestModel == null || userRestModel.hasError()) {
			return pesquisa;
		}

		// se pesquisa for por null, retorna todos os usuário
		if (termo == null) {
			userRestModel.getOutput().getResult().getUsuarios().forEach(m -> {
				UserDTO dto = m.toUserDTO();
				dto.setAvatar(recuperarAvatarNaApiPeloLogin(dto.getLogin())); 
				pesquisa.add(dto);
			});
			return pesquisa;
		}

		// passa o termo de busca pra lowercase e sai procurando alguma
		// ocorrencia em algum dos atributos do objeto
		termo = termo.toLowerCase();
		for (Usuario u : userRestModel.getOutput().getResult().getUsuarios()) {
			String concat = (
				u.getArea() + " " + u.getBirthDate() + " " + u.getEmail() + " " +
				u.getLinkedinUrl() + " " + u.getLogin() + " " + u.getUserName() + " "
			).toLowerCase();

			if (concat.contains(termo)) {
				UserDTO dto = u.toUserDTO();
				dto.setAvatar(recuperarAvatarNaApiPeloLogin(dto.getLogin())); 
				pesquisa.add(dto);
			}
		}

		// se não encontrar nada na pesquisa, retorna todos os usuários
		if (pesquisa.isEmpty()) {
			userRestModel.getOutput().getResult().getUsuarios().forEach(m -> {
				UserDTO dto = m.toUserDTO();
				dto.setAvatar(recuperarAvatarNaApiPeloLogin(dto.getLogin())); 
				pesquisa.add(dto);
			});
			return pesquisa;
		}
		return pesquisa;
	}

    
    public boolean existsByLogin(String login) {
        log.info("Verificando se usuário existe pelo login, {}", login);
        return userRepository.findByLogin(login) != null;
    }

    public boolean existsByLoginAndUserName(String login, String userName) {
        log.info("Verificando se usuário existe pelo login, {} e {}", login, userName);
        return userRepository.findByLoginAndUserName(login, userName) != null;
    }
    
    public Optional<User> findByLogin(String login) {
        log.info("Recuperando usuário pelo login, {}", login);
        return Optional.ofNullable(userRepository.findByLogin(login));
    }

	/**
	 * Busca um usuário (consultando a api de avatar) dado uma lista de usuários
	 * @param login
	 * @param users
	 * @return Optional<User>
	 */
	public Optional<User> findByLoginApi(String login, List<Usuario> users) {
		
		log.info("Recuperando usuário pelo login da api, {}", login);
		
        for (Usuario u : users) {
        	if (u.getLogin().equals(login)) {
				String avatar = recuperarAvatarNaApiPeloLogin(login);
				User user = u.toUser();
				user.setAvatar(avatar);
				return Optional.of(user);
        	}
		}
        return Optional.empty();
	}


	public UserRestModel carregarUsuariosApi() {
		return carregarUsuariosApi(TokenService.getToken());
	}

	/**
	 * Consulta a api externa da dminer para recuperar todos os usuários
	 * @param token
	 * @return UserRestModel
	 */
    public UserRestModel carregarUsuariosApi(String token) {
    	
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
			if (response.contains("expirou") || response.contains("não fez login")) {				
				userRestModel.getOutput().setMessages(Arrays.asList("Token expirado!", "Precisa fazer o login no sistema"));
				return userRestModel;
			}
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				userRestModel = gson.fromJson(response, UserRestModel.class);
				return userRestModel;				
			} catch (IllegalStateException e) {
				return null;
			}
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        return null;
    }
    

	/**
	 * Recupera os usuários da api externa e retorna uma lista de 
	 * usuários com informações reduzidas contendo login, userName e avatar
	 * @param token
	 * @param carregarAvatar
	 * @return List<UserReductDTO>
	 */
    public List<UserReductDTO> carregarUsuariosApiReduct(String token, boolean carregarAvatar) {
        log.info("Recuperando todos os usuário reduzidos na api externa");
        
    	userRestModel = carregarUsuariosApi(token);

        List<UserReductDTO> usuarios = new ArrayList<>();
        // UserRestModel model = carregarUsuariosApi(token);

        if (userRestModel == null || userRestModel.hasError()) {
			log.info("Nenhum usuário carregado da api");
        	return usuarios;
        }
        
        userRestModel.getOutput().getResult().getUsuarios().forEach(u -> {
        	UserReductDTO dto = new UserReductDTO();
        	dto.setLogin(u.getLogin());
        	dto.setUserName(u.getUserName());
			if (carregarAvatar)
				dto.setAvatar(recuperarAvatarNaApiPeloLogin(u.getLogin()));
        	usuarios.add(dto);
        });

    	return usuarios;
    }
    
    public UserDTO buscarUsuarioApi(String login, String token) {
        log.info("Recuperando todos os usuário na api externa");
        
        // UserRestModel model = carregarUsuariosApi(token);
    	userRestModel = carregarUsuariosApi(token);

        // System.out.println(userRestModel.toString());
        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
        
		UserDTO dto = new UserDTO();
        userRestModel.getOutput().getResult().getUsuarios().forEach(u -> {
			if (login.equals(u.getLogin())) {
				dto.setLogin(u.getLogin());
				dto.setUserName(u.getUserName());
				dto.setAvatar(recuperarAvatarNaApiPeloLogin(u.getLogin()));
				dto.setArea(u.getArea());
				dto.setBanner(getBannerString(login));
				dto.setBirthDate(u.getBirthDate());
				dto.setEmail(u.getEmail());
				dto.setLinkedinUrl(u.getLinkedinUrl());
				return;								
			}
        });
		return dto;
    }


	public UserReductDTO buscarUsuarioApiReduct(String login, String token) {
        log.info("Recuperando todos os usuário reduzidos na api externa");
        
    	userRestModel = carregarUsuariosApi(token);

        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
        
		UserReductDTO dto = new UserReductDTO();
        userRestModel.getOutput().getResult().getUsuarios().forEach(u -> {
			if (login.equals(u.getLogin())) {
				dto.setLogin(u.getLogin());
				dto.setUserName(u.getUserName());
				dto.setAvatar(recuperarAvatarNaApiPeloLogin(u.getLogin()));
				return;
			}
        });
		return dto;
    }

	
	public String getAvatarBase64ByLogin(String login) {
		try {
			String url = "https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=" + login;
			log.info("url avatar: {}", url);

    		BufferedImage image = ImageIO.read(new URL(url));
    		if (image != null) {
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				String avatar =  "data:image/png;base64," + new String(org.bouncycastle.util.encoders.Base64.encode(baos.toByteArray()));
				log.info("Imagem Base64: {}", avatar.substring(0, 80) + "..." + avatar.substring(avatar.length()-20, avatar.length()));
				return avatar;
    		}
    	} catch (IOException e) {}
		log.error("Nenhum avatar recuperado para o login: {}", login);
		return null;
	}


	public UserReductDTO buscarUsuarioApiReductTemp(String login) {
        log.info("Recuperando todos os usuário reduzidos na api externa");
        
    	userRestModel = carregarUsuariosApi(TokenService.getToken());

        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
        
		UserReductDTO dto = new UserReductDTO();
        userRestModel.getOutput().getResult().getUsuarios().forEach(u -> {
			if (login.equals(u.getLogin())) {
				dto.setLogin(u.getLogin());
				dto.setUserName(u.getUserName());
				dto.setAvatar(recuperarAvatarNaApiPeloLogin(u.getLogin()));
				return;
			}
        });
		return dto;
    }


	/**
	 * Recupera o avatar do login, transforma em base64 e retorna
	 * @param login
	 * @return Avatar em base64
	 */
	public String recuperarAvatarNaApiPeloLogin(String login) {
		try {
			String url = "https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=" + login;
			log.info("url avatar: {}", url);

    		BufferedImage image = ImageIO.read(new URL(url));
    		if (image != null) {
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				String avatar =  "data:image/png;base64," + new String(org.bouncycastle.util.encoders.Base64.encode(baos.toByteArray()));
				log.info("Imagem Base64: {}", avatar.substring(0, 80) + "..." + avatar.substring(avatar.length()-20, avatar.length()));
				return avatar;
    		}
    	} catch (IOException e) {}
		log.error("Nenhum avatar recuperado para o login: {}", login);
		return null;
	}
    
	public List<UserDTO> getAniversariantes(String token) {
		List<UserDTO> aniversariantes = new ArrayList<UserDTO>();
		userRestModel = carregarUsuariosApi(token);

        if (userRestModel == null) {    		
    		return aniversariantes;
    	}

		userRestModel.getOutput().getResult().getUsuarios().forEach(u -> {
        	if (u.getBirthDate() != null && UtilDataHora.isAniversariante(u.getBirthDate())) {
				UserDTO dto = u.toUserDTO();
				dto.setAvatar(recuperarAvatarNaApiPeloLogin(u.getLogin()));
        		aniversariantes.add(dto);
        	}
        });
		
		return aniversariantes;
	}



    /**
     * Recupera o banner do usuário no banco de dados
     * @param login
     * @return byte[]
     */
    public byte[] getBanner(String login) {
		User user = userRepository.findByLogin(login);
		if (user == null || user.getBanner() == null) 
			return null;
		return user.getBanner().getBytes();
    }

	public String getBannerString(String login) {
		User user = userRepository.findByLogin(login);
		if (user == null || user.getBanner() == null) 
			return null;
		return user.getBanner();
    }
}
