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
import com.dminer.rest.DminerWebService;
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
    
	// @Autowired
	// private DminerWebService dminerWebService;


	private static final Logger log = LoggerFactory.getLogger(UserService.class);


    @Override
    public User persist(User user) {
        log.info("Persistindo usuário: {}", user.toString());
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
    	

	// public List<UserDTO> getAllUsersDto(UserRestModel<Usuario> usuarios, String token, boolean avatar, boolean banner) {

	// 	if (usuarios == null || usuarios.hasError()) {
	// 		log.info("Erro ao buscar usuários da api externa");
	// 		return null;
	// 	}
        
    //     List<UserDTO> userList = new ArrayList<>();
    //     usuarios.getUsers().parallelStream().forEach(usuario -> {
    //         UserDTO userDto = usuario.toUserDTO();
    //     	userList.add(userDto);
    //     });

	// 	// se avatar == true, busca os avatares enquanto trata dos banners
	// 	UserRestModel<UserAvatar> usuariosAvatar = null;
	// 	if (avatar) {			
	// 		usuariosAvatar = getAllAvatarCustomer(token);
	// 		log.info("Buscando avatares: {} registros encontrados", usuariosAvatar.getUsers().size());
	// 	}

	// 	if (banner) {
	// 		userList.parallelStream().forEach(usuario -> {
	// 			String bannerTemp = getBannerByLogin(usuario.getLogin());
	// 			usuario.setBanner(bannerTemp);
	// 		});
	// 	}

	// 	if (avatar && usuariosAvatar != null) {
	// 		UserRestModel<UserAvatar> usuariosAvatarTemp = usuariosAvatar;
	// 		usuariosAvatar = null;			
	// 		userList.parallelStream().forEach(usuario -> {
	// 			usuario.setAvatar(getAvatarByUsername(usuariosAvatarTemp, usuario.getUserName()));
	// 		});
	// 	}
	// 	return userList;
	// }


	// public List<UserDTO> getAllUsersDto(String token, boolean avatar, boolean banner) {		
	// 	UserRestModel<Usuario> usuarios = carregarUsuariosApi(token);
	// 	return getAllUsersDto(usuarios, token, avatar, banner);
	// }
	

	public List<UserDTO> search(String termo, String token) {

		List<UserDTO> pesquisa = new ArrayList<UserDTO>();

		// UserRestModel<Usuario> userRestModel = carregarUsuariosApi(token);
		UserRestModel<Usuario> userRestModel = DminerWebService.getInstance().getUsuariosApi(token);

		// se vier null ou conter erros, retorna lista vazia
		if (userRestModel == null || userRestModel.hasError()) {
			return pesquisa;
		}

		// se pesquisa for por null, retorna todos os usuário
		if (termo == null) {
			userRestModel.getUsuarios().forEach(user -> {
				pesquisa.add(user.toUserDTO(true));
			});
			return pesquisa;
		}

		// passa o termo de busca pra lowercase e sai procurando alguma
		// ocorrencia em algum dos atributos do objeto
		termo = termo.toLowerCase();
		for (Usuario u : userRestModel.getUsuarios()) {
			String concat = (
				u.getArea() + " " + u.getBirthDate() + " " + u.getEmail() + " " +
				u.getLinkedinUrl() + " " + u.getLogin() + " " + u.getUserName() + " "
			).toLowerCase();

			if (concat.contains(termo)) {
				pesquisa.add(u.toUserDTO(true));
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
        // log.info("Verificando se usuário existe pelo login no repositório, {}", login);
		if (login == null) return false;
        return userRepository.findByLogin(login) != null;
    }


    public boolean existsByLoginAndUserName(String login, String userName) {
        // log.info("Verificando se usuário existe pelo login no repositório, {} e {}", login, userName);
		if (login == null || userName == null) return false;
        return userRepository.findByLoginAndUserName(login, userName) != null;
    }

	public Optional<User> findByLoginAndUserName(String login, String userName) {
        // log.info("Verificando se usuário existe pelo login no repositório, {} e {}", login, userName);
		return Optional.ofNullable(userRepository.findByLoginAndUserName(login, userName));
    }

    /**
	 * Busca um usuário pelo login no banco de dados
	 * @param login
	 * @return
	 */
    public Optional<User> findByLogin(String login) {
        // log.info("Recuperando usuário pelo login no repositório, {}", login);
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
		
		// UserRestModel<Usuario> users = carregarUsuariosApi(token);
		UserRestModel<Usuario> users = DminerWebService.getInstance().getUsuariosApi(token);

		if (users == null || users.hasError()) {
			return Optional.empty();
		}

		Optional<Usuario> findFirst = users.getUsuarios().parallelStream().filter(user -> 
			user.getLogin().equals(login)
		).findFirst();
		
		if (findFirst.isPresent()) return Optional.of(findFirst.get().toUser(true));
        return Optional.empty();
	}

	
    

    public List<UserReductDTO> carregarUsuariosApiReductDto(String token, boolean avatar) {

        log.info("Recuperando todos os usuários reduzidos na api externa");
        
		if (token == null) {
			return null;
		}
		
		
		// UserRestModel<Usuario> userRestModel = carregarUsuariosApi(token);
		UserRestModel<Usuario> userRestModel = DminerWebService.getInstance().getUsuariosApi(token);

        List<UserReductDTO> usuarios = new ArrayList<>();

		if (userRestModel == null) {
			return usuarios;
		}

        if (userRestModel.hasError()) {
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
        
		userRestModel.getUsuarios().parallelStream().forEach(user -> {
			UserReductDTO dto = user.toUserReductDTO(avatar);
			usuarios.add(dto);
		});

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
        
        // UserRestModel<Usuario> userRestModel = carregarUsuariosApi(token);
		UserRestModel<Usuario> userRestModel = DminerWebService.getInstance().getUsuariosApi(token);

        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
        
		for (Usuario u : userRestModel.getUsuarios()) {
			if (login.equals(u.getLogin())) {
				return u.toUserDTO(true);				
			}			
		}
		return null;
    }


	public UserReductDTO buscarUsuarioApiReduct(String login, String token) {
        log.info("Recuperando todos os usuário reduzidos na api externa");
        
		// UserRestModel<Usuario> userRestModel = carregarUsuariosApi(token);
		UserRestModel<Usuario> userRestModel = DminerWebService.getInstance().getUsuariosApi(token);

        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
		return buscarUsuarioApiReduct(userRestModel, login);
    }


	public UserReductDTO buscarUsuarioApiReduct(UserRestModel<Usuario> userRestModel, String login) {
		UserReductDTO dto = new UserReductDTO();
		for (Usuario u : userRestModel.getOutput().getResult().getUsuarios()) {
			if (login.equals(u.getLogin())) {
				dto = u.toUserReductDTO(true);
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


	public String getBannerByLogin(String login) {
		User user = userRepository.findByLogin(login);
		if (user == null || user.getBanner() == null) 
			return null;
		return user.getBanner();
    }


	public List<UserDTO> getAniversariantes(String token, boolean buscarAvatar) {

		// UserRestModel<Usuario> usuarios = carregarUsuariosApi(token);
		UserRestModel<Usuario> usuarios = DminerWebService.getInstance().getUsuariosApi(token);

        if (usuarios == null || usuarios.hasError()) {
			log.info("Erro ao buscar aniversariantes");
			return null;
		}
        
        List<UserDTO> aniversariantes = new ArrayList<UserDTO>();
        usuarios.getOutput().getResult().getUsuarios().forEach(usuario -> {        	
        	if (usuario.getBirthDate() != null && UtilDataHora.isAniversariante(usuario.getBirthDate())) {
        		aniversariantes.add(usuario.toUserDTO(buscarAvatar));
        	}
        });
        
		return aniversariantes;
	}

	public void atualizarDadosNoBancoComApiExterna(UserRestModel<Usuario> usuarios) {				
		for (Usuario usuario : usuarios.getUsuarios()) {
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

	// public void inserirDadosNoBancoComApiExterna() {
	// 	String token = TokenService.getToken();
	// 	UserRestModel<Usuario> usuarios = carregarUsuariosApi(token);
	// 	for (Usuario usuario : usuarios.getUsers()) {
	// 		User u = usuario.toUser();
	// 		u = persist(u);			
	// 	}
	// }

	

}
