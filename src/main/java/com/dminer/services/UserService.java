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
import com.dminer.utils.UtilFilesStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class UserService implements IUserService {

    @Autowired
	private UserRepository userRepository;
    
	// @Autowired
	// private TokenService tokenService;

	private static final Logger log = LoggerFactory.getLogger(UserService.class);


	private UserRestModel userRestModel;
	


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

		userRestModel = carregarUsuariosApi(token);

		// se vier null ou conter erros, retorna lista vazia
		if (userRestModel == null || userRestModel.hasError()) {
			return pesquisa;
		}

		// se pesquisa for por null, retorna todos os usuário
		if (termo == null) {
			userRestModel.getOutput().getResult().getUsuarios().forEach(m -> {
				pesquisa.add(m.toUserDTO());
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
				pesquisa.add(u.toUserDTO());
			}
		}

		// se não encontrar nada na pesquisa, retorna todos os usuários
		if (pesquisa.isEmpty()) {
			userRestModel.getOutput().getResult().getUsuarios().forEach(m -> {
				pesquisa.add(m.toUserDTO());
			});
			return pesquisa;
		}

		return pesquisa;
	}


	public List<UserDTO> search(String termo, List<UserDTO> users) {

		List<UserDTO> pesquisa = new ArrayList<UserDTO>();

		// se vier null ou conter erros, retorna lista vazia
		if (users == null || users.isEmpty()) {
			return pesquisa;
		}

		// se pesquisa for por null, retorna todos os usuário
		if (termo == null) {			
			return users;
		}

		// passa o termo de busca pra lowercase e sai procurando alguma
		// ocorrencia em algum dos atributos do objeto
		termo = termo.toLowerCase();
		for (UserDTO u : users) {
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

	public void atualizarDadosNoBancoComApiExterna(UserRestModel usuarios) {				
		for (Usuario usuario : usuarios.getOutput().getResult().getUsuarios()) {
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


	public Optional<User> findByLoginApi(String login, List<Usuario> users) {
		
		log.info("Recuperando usuário pelo login da api, {}", login);
		
		if (existsByLogin(login)) {
			return findByLogin(login);
		}

        for (Usuario u : users) {
        	if (u.getLogin().equals(login)) {
				User user = persist(new User(u.getLogin(), u.getUserName()));
				return Optional.ofNullable(user);
        	}			
		}
        return Optional.empty();
	}

    
    public UserRestModel carregarUsuariosApi(String token) {
    	
		if (token == null) {
			return null;
		}

		if (userRestModel != null ) {
			if (userRestModel.getOutput().getResult().getUsuarios() != null && !userRestModel.getOutput().getResult().getUsuarios().isEmpty()) {
				return userRestModel;
			}
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
			if (response.contains("expirou") || response.contains("não fez login") || response.contains("fezologinnosistema") || response.contains("Hum...")) {
				userRestModel = new UserRestModel();
				userRestModel.getOutput().setMessages(Arrays.asList("Token expirado!", "Precisa fazer o login no sistema", token));
				return userRestModel;
			}
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				System.out.println(response.toString());
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
			if (carregarAvatar) {
				dto.setAvatar(getAvatarBase64ByLogin(u.getLogin()));
			}
        	usuarios.add(dto);
        });

    	return usuarios;
    }
    
    public UserDTO buscarUsuarioApi(String login, String token) {
        log.info("Recuperando todos os usuário na api externa");
        
        // UserRestModel model = carregarUsuariosApi(token);
		if (userRestModel == null) {
			userRestModel = carregarUsuariosApi(token);
		}

        // System.out.println(userRestModel.toString());
        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
        
		UserDTO dto = new UserDTO();
        userRestModel.getOutput().getResult().getUsuarios().forEach(u -> {
			if (login.equals(u.getLogin())) {
				dto.setLogin(u.getLogin());
				dto.setUserName(u.getUserName());
				dto.setAvatar(getAvatarBase64ByLogin(u.getLogin()));
				dto.setArea(u.getArea());
				dto.setBanner(getBannerString(login));
				dto.setBirthDate(u.getBirthDate());
				dto.setEmail(u.getEmail());
				dto.setLinkedinUrl(u.getLinkedinUrl());				
			}
        });
		return dto;
    }

	// public UserReductDTO buscarUsuarioApiReduct(String login) {
	// 	return buscarUsuarioApiReduct(login, TokenService.getToken());
	// }

	public UserReductDTO buscarUsuarioApiReduct(String login, String token) {
        log.info("Recuperando todos os usuário reduzidos na api externa");
        
		if (userRestModel == null) {
			log.info("Carregando usuário diretamente da API");
			userRestModel = carregarUsuariosApi(token);
		}

        if (userRestModel == null || userRestModel.hasError()) {
			return null;
        }
        
		UserReductDTO dto = new UserReductDTO();
		for (Usuario u : userRestModel.getOutput().getResult().getUsuarios()) {
			if (login.equals(u.getLogin())) {
				dto.setLogin(u.getLogin());
				dto.setUserName(u.getUserName());
				dto.setAvatar(getAvatarBase64ByLogin(u.getLogin()));
				return dto;
			}			
		}
		return dto;
    }



	public String getAvatarBase64(String pathFile) {
    	try {
    		byte[] image = UtilFilesStorage.loadImage(pathFile);
    		if (image != null) {
    			String base64AsString = "data:image/png;base64," + new String(org.bouncycastle.util.encoders.Base64.encode(image));
    			log.info("Imagem Base64: {}", base64AsString.substring(0, 80) + "..." + base64AsString.substring(base64AsString.length()-20, base64AsString.length()));
    			return base64AsString;
    		}
    	} catch (IOException e) {}
    	return null;
    }
    
    
	private String montarCaminhoAvatarDiretorio(String login) {
		String root = UtilFilesStorage.getProjectPath() + UtilFilesStorage.separator + "avatares";
		String name = login.replace('.', '-') + "-resized.png";
		return root + UtilFilesStorage.separator + name;
	}


    /**
     * Verifica se o avatar existe no diretório "avatares", caso não existe, recupera na api
     * https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=?
     * salva no diretório e retorna uma string contendo a url do arquivo
     * @param login
     * @return String
     */
    public String getAvatarDir(String login) {
		String imagemRedimensionadaPath = montarCaminhoAvatarDiretorio(login);
		
		if (UtilFilesStorage.fileExists(imagemRedimensionadaPath)) {
			System.out.println("Arquivo já existe!! -> " + imagemRedimensionadaPath);
			return imagemRedimensionadaPath;
		}		
		return gravarAvatarDiretorio(login);
    }
    

	public String gravarAvatarDiretorio(String login) {
		try {
    		String root = UtilFilesStorage.getProjectPath() + UtilFilesStorage.separator + "avatares";
			String caminho = montarCaminhoAvatarDiretorio(login);

    		UtilFilesStorage.createDirectory(root);
    		BufferedImage image = ImageIO.read(new URL("https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=" + login));
    		if (image != null) {
    			UtilFilesStorage.saveImage(caminho, image);
    			//ImageResizer.resize(caminho, caminho, 0.5);
				return caminho;
    		}
    	} catch (IOException e) {}
		return null;
	}

    
    public String getAvatarBase64ByLogin(String login) {
		String dir = this.getAvatarDir(login);
		if (dir != null) {
			return this.getAvatarBase64(dir);
		}
    	return null;
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
