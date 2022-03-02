package com.dminer.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.dminer.components.TokenService;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.User;
import com.dminer.repository.UserRepository;
import com.dminer.rest.model.users.UserAvatar;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
import com.dminer.services.interfaces.IUserService;
import com.dminer.utils.UtilDataHora;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Service
public class UserService implements IUserService {

    @Autowired
	private UserRepository userRepository;
    
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
    	

	public List<UserDTO> getAllUsersDto(UserRestModel<Usuario> usuarios, String token, boolean avatar, boolean banner) {

		log.info("get all users dto, {} registros", usuarios.getUsers().size());

		if (usuarios == null || usuarios.hasError()) {
			log.info("Erro ao buscar usuários da api externa");
			return null;
		}
        
        List<UserDTO> userList = new ArrayList<>();
        usuarios.getUsers().parallelStream().forEach(usuario -> {
            UserDTO userDto = usuario.toUserDTO();
        	userList.add(userDto);
        });

		// se avatar == true, busca os avatares enquanto trata dos banners
		UserRestModel<UserAvatar> usuariosAvatar = null;
		if (avatar) {			
			usuariosAvatar = getAllAvatarCustomer(token);
			log.info("Buscando avatares: {} registros encontrados", usuariosAvatar.getUsers().size());
		}

		if (banner) {
			userList.parallelStream().forEach(usuario -> {
				String bannerTemp = getBannerByLogin(usuario.getLogin());
				usuario.setBanner(bannerTemp);
			});
		}

		if (avatar && usuariosAvatar != null) {
			UserRestModel<UserAvatar> usuariosAvatarTemp = usuariosAvatar;
			usuariosAvatar = null;			
			userList.parallelStream().forEach(usuario -> {
				usuario.setAvatar(getAvatarByUsername(usuariosAvatarTemp, usuario.getUserName()));
			});
		}
		return userList;
	}


	public List<UserDTO> getAllUsersDto(String token, boolean avatar, boolean banner) {		
		UserRestModel<Usuario> usuarios = carregarUsuariosApi(token);
		return getAllUsersDto(usuarios, token, avatar, banner);
	}
	

	public List<UserDTO> search(String termo, String token, boolean avatar) {

		List<UserDTO> pesquisa = new ArrayList<UserDTO>();

		UserRestModel<Usuario> userRestModel = carregarUsuariosApi(token);

		// se vier null ou conter erros, retorna lista vazia
		if (userRestModel == null || userRestModel.hasError()) {
			return pesquisa;
		}

		// se avatar == true, busca os avatares enquanto trata dos banners
		UserRestModel<UserAvatar> usuariosAvatar = null;
		if (avatar) {
			usuariosAvatar = getAllAvatarCustomer(token);
		}

		// se pesquisa for por null, retorna todos os usuário
		if (termo == null) {
			userRestModel.getUsers().forEach(user -> {
				pesquisa.add(user.toUserDTO());
			});

			if (avatar && usuariosAvatar != null) {
				UserRestModel<UserAvatar> usuariosAvatarTemp = usuariosAvatar;
				usuariosAvatar = null;			
				pesquisa.parallelStream().forEach(usuario -> {
					usuario.setAvatar(getAvatarByUsername(usuariosAvatarTemp, usuario.getUserName()));
				});
			}
			return pesquisa;
		}

		// passa o termo de busca pra lowercase e sai procurando alguma
		// ocorrencia em algum dos atributos do objeto
		termo = termo.toLowerCase();
		for (Usuario u : userRestModel.getUsers()) {
			String concat = (
				u.getArea() + " " + u.getBirthDate() + " " + u.getEmail() + " " +
				u.getLinkedinUrl() + " " + u.getLogin() + " " + u.getUserName() + " "
			).toLowerCase();

			if (concat.contains(termo)) {
				pesquisa.add(u.toUserDTO());
			}
		}

		// se não encontrar nada na pesquisa, retorna todos os usuários
		if (! pesquisa.isEmpty()) {
			userRestModel.getOutput().getResult().getUsuarios().forEach(user -> {
				pesquisa.add(user.toUserDTO());
			});

			if (avatar && usuariosAvatar != null) {
				UserRestModel<UserAvatar> usuariosAvatarTemp = usuariosAvatar;
				usuariosAvatar = null;			
				pesquisa.parallelStream().forEach(usuario -> {
					usuario.setAvatar(getAvatarByUsername(usuariosAvatarTemp, usuario.getUserName()));
				});
			}
		}

		return pesquisa;
	}


	/**
	 * Método usado a principio na api search de aniversariantes
	 * @param usuarios
	 * @param termo
	 * @param token
	 * @return
	 */
	public List<UserDTO> search(List<UserDTO> usuarios, String termo) {

		List<UserDTO> pesquisa = new ArrayList<UserDTO>();

		// se pesquisa for por null, retorna todos os usuário
		if (termo == null || usuarios == null) {			
			return pesquisa;
		}

		// passa o termo de busca pra lowercase e sai procurando alguma
		// ocorrencia em algum dos atributos do objeto
		termo = termo.toLowerCase();
		for (UserDTO u : usuarios) {
			String concat = (
				u.getArea() + " " + u.getBirthDate() + " " + u.getEmail() + " " +
				u.getLinkedinUrl() + " " + u.getLogin() + " " + u.getUserName() + " "
			).toLowerCase();

			if (concat.contains(termo)) {
				pesquisa.add(u);
			}
		}
		return pesquisa;
	}

	
    public boolean existsByLogin(String login) {
        log.info("Verificando se usuário existe pelo login no repositório, {}", login);
		if (login == null) return false;
        return userRepository.findByLogin(login) != null;
    }


    public boolean existsByLoginAndUserName(String login, String userName) {
        log.info("Verificando se usuário existe pelo login no repositório, {} e {}", login, userName);
		if (login == null || userName == null) return false;
        return userRepository.findByLoginAndUserName(login, userName) != null;
    }

    /**
	 * Busca um usuário pelo login no banco de dados
	 * @param login
	 * @return
	 */
    public Optional<User> findByLogin(String login) {
        log.info("Recuperando usuário pelo login no repositório, {}", login);
        return Optional.ofNullable(userRepository.findByLogin(login));
    }


	/**
	 * Busca um usuário pelo login na api dminer
	 * @param login
	 * @param users
	 * @return
	 */
	public Optional<User> findByLoginApi(String token, String login) {
		
		log.info("Recuperando usuário pelo login da api, {}", login);
		
		UserRestModel<Usuario> users = carregarUsuariosApi(token);
		if (users == null || users.hasError()) {
			return Optional.empty();
		}

		Optional<Usuario> findFirst = users.getUsers().parallelStream().filter(user -> 
			user.getLogin().equals(login)
		).findFirst();
		
		if (findFirst.isPresent()) return Optional.of(findFirst.get().toUser());
        return Optional.empty();
	}

	@Async
    public UserRestModel<Usuario> carregarUsuariosApi(String token) {
    	
		if (token == null) {
			return null;
		}

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
			
			UserRestModel<Usuario> userRestModel = new UserRestModel<Usuario>();
			if (response.contains("expirou") || response.contains("não fez login") || response.contains("fezologinnosistema") || response.contains("Hum...")) {
				log.info("Algo errado ao recuperar os usuários da api: {}", response);
				userRestModel.getOutput().setMessages(Arrays.asList("Token expirado!", "Precisa fazer o login no sistema", token));
				return userRestModel;
			}
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				return gson.fromJson(response, new TypeToken<UserRestModel<Usuario>>(){}.getType());
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
    

    public List<UserReductDTO> carregarUsuariosApiReductDto(String token, boolean avatar) {
        log.info("Recuperando todos os usuários reduzidos na api externa");
        
		if (token == null) {
			return null;
		}
		
		UserRestModel<Usuario> userRestModel = carregarUsuariosApi(token);

        List<UserReductDTO> usuarios = new ArrayList<>();

        if (userRestModel == null || userRestModel.hasError()) {
			log.info("Nenhum usuário carregado da api em: carregarUsuariosApiReductDto");
			if (userRestModel.hasError()) {
				userRestModel.getOutput().getMessages().forEach(message -> {
					log.info("Messagem: {}", message);
				});
			}

			if (userRestModel.getOutput().getResult().getUsuarios().isEmpty()) {
				log.info("Messagem: Coleção de usuarios tá vazia");
			}
        	return usuarios;
        }
        
		UserRestModel<UserAvatar> usuariosAvatar = null;
		if (avatar) {
			usuariosAvatar = getAllAvatarCustomer(token);
		}

		if (usuariosAvatar != null) {
			UserRestModel<UserAvatar> usuariosAvatarTemp = usuariosAvatar;
			usuariosAvatar = null;

			userRestModel.getUsers().parallelStream().forEach(user -> {
				UserReductDTO dto = user.toUserReductDTO();
				dto.setAvatar(getAvatarByUsername(usuariosAvatarTemp, user.getUserName()));
				usuarios.add(dto);
			});
		}

    	return usuarios;
    }
    

	/**
	 * Busca o usuário na api, incluindo o avatar
	 * @param login
	 * @param token
	 * @return
	 */
    public UserDTO buscarUsuarioApi(String login, String token) {
        log.info("Recuperando todos os usuário na api externa");
        
        UserRestModel<Usuario> userRestModel = carregarUsuariosApi(token);
		
        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
        
		UserDTO dto = new UserDTO();
        userRestModel.getOutput().getResult().getUsuarios().forEach(u -> {
			if (login.equals(u.getLogin())) {
				dto.setLogin(u.getLogin());
				dto.setUserName(u.getUserName());
				dto.setAvatar(getAvatarEndpoint(u.getLogin()));
				dto.setArea(u.getArea());
				dto.setBanner(getBannerByLogin(login));
				dto.setBirthDate(u.getBirthDate());
				dto.setEmail(u.getEmail());
				dto.setLinkedinUrl(u.getLinkedinUrl());				
			}
        });
		return dto;
    }


	public UserReductDTO buscarUsuarioApiReduct(String login, String token) {
        log.info("Recuperando todos os usuário reduzidos na api externa");
        
		UserRestModel<Usuario> userRestModel = carregarUsuariosApi(token);
		if (userRestModel == null) {
			log.info("Carregando usuário diretamente da API");
			userRestModel = carregarUsuariosApi(token);
		}

        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
		return buscarUsuarioApiReduct(userRestModel, login);
    }


	public UserReductDTO buscarUsuarioApiReduct(UserRestModel<Usuario> userRestModel, String login) {
		UserReductDTO dto = new UserReductDTO();
		for (Usuario u : userRestModel.getOutput().getResult().getUsuarios()) {
			if (login.equals(u.getLogin())) {
				dto.setLogin(u.getLogin());
				dto.setUserName(u.getUserName());
				dto.setAvatar(getAvatarEndpoint(u.getLogin()));
				return dto;
			}			
		}
		return null;
	}

       
	/**
	 * Recupera o avatar de um único usuário no endpoint dminer
	 * @param login
	 * @return string contendo avatar em base 64
	 */
    public String getAvatarEndpoint(String login) {
		try {
			BufferedImage image = ImageIO.read(new URL("https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=" + login));
    		if (image != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				byte[] bytes = baos.toByteArray();
    			return "data:image/png;base64," + new String(org.bouncycastle.util.encoders.Base64.encode(bytes));
    		}
    	} catch (IOException e) {}
    	return null;
	}


	private String getBannerByLogin(String login) {
		User user = userRepository.findByLogin(login);
		if (user == null || user.getBanner() == null) 
			return null;
		return user.getBanner();
    }


	@Async
	public UserRestModel<UserAvatar> getAllAvatarCustomer(String token) {

		if (token == null) {
			return null;
		}

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
			
			UserRestModel<UserAvatar> userRestModel = new UserRestModel<UserAvatar>();
			if (retornoTokenInvalidoApi(response)) {
				log.info("retornoTokenInvalidoApi: {}", response);
				userRestModel.getOutput().setMessages(Arrays.asList("Token expirado!", "Precisa fazer o login no sistema", token));
				return userRestModel;
			}
			
			if (retornoInvalidoApi(response)) {
				log.info("retornoInvalidoApi: {}", response);
				userRestModel.getOutput().setMessages(Arrays.asList("Verifique se você tem autorização adequada", "Entre em contato com o suporte", token));
				return userRestModel;
			}

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				userRestModel = gson.fromJson(response, new TypeToken<UserRestModel<UserAvatar>>(){}.getType());
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
	 * Busca o avatar de um usuário pelo username
	 * @param usuarios
	 * @return avatar em base 64
	 */
	private String getAvatarByUsername(UserRestModel<UserAvatar> usuarios, String userName) {
		UserAvatar userAvatar = usuarios.getUsers().stream().filter(usuario -> 
			usuario.getUserName().equals(userName)
		).findFirst().orElse(null);
		if (userAvatar == null || userAvatar.isCommonAvatar()) {
			return "data:image/png;base64," + usuarios.getOutput().getResult().getCommonAvatar();
		}
		return "data:image/png;base64," + userAvatar.getAvatar();
	}

	public List<UserDTO> getAniversariantes(String token, boolean buscarAvatar) {		
		UserRestModel<Usuario> usuarios = carregarUsuariosApi(token);

        if (usuarios == null || usuarios.hasError()) {
			log.info("Erro ao buscar aniversariantes");
			return null;
		}
        
        List<UserDTO> aniversariantes = new ArrayList<UserDTO>();
        usuarios.getOutput().getResult().getUsuarios().forEach(usuario -> {        	
        	if (usuario.getBirthDate() != null && UtilDataHora.isAniversariante(usuario.getBirthDate())) {
        		aniversariantes.add(usuario.toUserDTO());
        	}
        });
        
		if (buscarAvatar) {
			log.info("Recuperando os avatares de cada aniversariante");
			UserRestModel<UserAvatar> usuariosAvatar = getAllAvatarCustomer(token);
			if (usuariosAvatar != null) {
				aniversariantes.forEach(aniversariante -> {
					String avatar = getAvatarByUsername(usuariosAvatar, aniversariante.getUserName());
					aniversariante.setAvatar(avatar);
				});
			}
		}
		return aniversariantes;
	}

	public void atualizarDadosNoBancoComApiExterna(UserRestModel<Usuario> usuarios) {				
		for (Usuario usuario : usuarios.getUsers()) {
			User u = new User();
			if (!existsByLoginAndUserName(usuario.getLogin(), usuario.getUserName())) {
				if (existsByLogin(usuario.getLogin())) {
					u = findByLogin(usuario.getLogin()).get();					
				}
				u.setUserName(usuario.getUserName());
				u.setLogin(usuario.getLogin());
				persist(u);
			}
		}
	}

	public void inserirDadosNoBancoComApiExterna() {
		String token = TokenService.getToken();
		UserRestModel<Usuario> usuarios = carregarUsuariosApi(token);
		for (Usuario usuario : usuarios.getUsers()) {
			User u = usuario.toUser();
			u = persist(u);			
		}
	}

	private boolean retornoInvalidoApi(String response) {

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

	private boolean retornoTokenInvalidoApi(String response) {

		response = response.toLowerCase();
		return (
			response.contains("expirou") || 
			response.contains("não fez login") || 
			response.contains("fezologinnosistema") || 
			response.contains("hum...")
		);		
	}

}
