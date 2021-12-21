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

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    
    public List<UserDTO> search(String termo) {
    	String token = getToken();
    	return search(termo, token);
    }
    
    public List<UserDTO> search(String termo, String token) {

		// carrega os usuario da api
    	UserRestModel model = carregarUsuariosApi(token);    	
    	List<UserDTO> pesquisa = new ArrayList<UserDTO>();
    	
		// se vier null ou conter erros, retorna lista vazia
    	if (model == null || model.hasError()) {
        	return pesquisa;
        }
    	
		// se pesquisa for por null, retorna todos os usuário
    	if (termo == null) {
    		model.getOutput().getResult().getUsuarios().forEach(m -> {
    			pesquisa.add(m.toUserDTO());
    		});
    		return pesquisa;
    	}
    	
		// passa o termo de busca pra lowercase e sai procurando alguma
		// ocorrencia em algum dos atributos do objeto
    	termo = termo.toLowerCase();
    	for (Usuario u : model.getOutput().getResult().getUsuarios()) {
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
			model.getOutput().getResult().getUsuarios().forEach(m -> {
				pesquisa.add(m.toUserDTO());
    		});
    		return pesquisa;
		}

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


    public Optional<User> findByLoginApi(String login) {
        log.info("Recuperando usuário pelo login da api, {}", login);
        if (existsByLogin(login)) {			        			
			return findByLogin(login);
		}
        return findByLoginOnlyApi(login);		
    }
    
    public Optional<User> findByLoginOnlyApi(String login) {
		Response<List<UserReductDTO>> retorno = carregarUsuariosApiReduct(getToken());
		if (!retorno.getErrors().isEmpty()) {
			Optional.empty();
		}

        List<UserReductDTO> users = retorno.getData();
        for (UserReductDTO u : users) {
        	if (u.getLogin().equals(login)) {
        		//if (! existsByLogin(u.getLogin())) {
        			User user = persist(new User(u.getLogin(), u.getUserName()));        			
        			return Optional.ofNullable(user);
        		//}
        	}			
		}
        return Optional.empty();
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
    
    
    public UserRestModel carregarUsuariosApi(String token) {
    	
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
			if (response.contains("expirou")) {
				UserRestModel staff = new UserRestModel();
				staff.getOutput().setMessages(Arrays.asList("Token expirado!"));
				return staff;
			}
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				UserRestModel staff = gson.fromJson(response, UserRestModel.class);
				return staff;				
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
    
    
    
    public Response<List<UserReductDTO>> carregarUsuariosApiReduct(String token) {
        log.info("Recuperando todos os usuário reduzidos na api externa");
        
		Response<List<UserReductDTO>> response = new Response<>();
        List<UserReductDTO> usuarios = new ArrayList<>();
        UserRestModel model = carregarUsuariosApi(token);
        System.out.println(model.toString());
        if (model == null || model.hasError()) {
			model.getOutput().getMessages().forEach(e -> response.getErrors().add(e));
        	return response;
        }
        
        model.getOutput().getResult().getUsuarios().forEach(u -> {
        	UserReductDTO dto = new UserReductDTO();
        	dto.setLogin(u.getLogin());
        	dto.setUserName(u.getUserName());
			dto.setAvatar(getAvatarBase64ByLogin(u.getLogin()));
        	usuarios.add(dto);
        });

		response.setData(usuarios);
    	return response;
    }
    
    public UserReductDTO carregarUsuarioApiReduct(String login, String token) {
        log.info("Recuperando todos os usuário reduzidos na api externa");
        
        UserRestModel model = carregarUsuariosApi(token);
        System.out.println(model.toString());
        if (model == null || model.hasError()) {
			return null;
        }
        
		UserReductDTO dto = new UserReductDTO();
        model.getOutput().getResult().getUsuarios().forEach(u -> {
			if (login.equals(u.getLogin())) {
				dto.setLogin(u.getLogin());
				dto.setUserName(u.getUserName());
				dto.setAvatar(getAvatarBase64ByLogin(u.getLogin()));
			}
        });
		return dto;
    }


    /**
     * Recupera o avatar no diretório "avatares" e transoforma em Base64
     * @param pathFile
     * @return String
     */
	@Deprecated
    public String getAvatarBase64_old(String pathFile) {
    	try {
    		byte[] image = UtilFilesStorage.loadImage(pathFile);
    		if (image != null) {
    			String base = Base64.getEncoder().encodeToString(image);
    			System.out.println(base);
    			return base;
    		}
    	} catch (IOException e) {}
    	return null;
    }


	public String getAvatarBase64(String pathFile) {
    	try {
    		byte[] image = UtilFilesStorage.loadImage(pathFile);
    		if (image != null) {
    			String base64AsString = "data:image/png;base64," + new String(org.bouncycastle.util.encoders.Base64.encode(image));
    			System.out.println(base64AsString);
    			return base64AsString;
    		}
    	} catch (IOException e) {}
    	return null;
    }
    
    
    /**
     * Verifica se o avatar existe no diretório "avatares", caso não existe, recupera na api
     * https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=?
     * salva no diretório e retorna uma string contendo a url do arquivo
     * @param login
     * @return String
     */
    public String getAvatarDir(String login) {
    	try {
    		String root = UtilFilesStorage.getProjectPath() + UtilFilesStorage.separator + "avatares";
    		String name = login.replace('.', '-') + "-resized.png";
    		String imagemRedimensionadaPath = root + UtilFilesStorage.separator + name;

    		if (UtilFilesStorage.fileExists(imagemRedimensionadaPath)) {
    			System.out.println("Arquivo já existe!! -> " + imagemRedimensionadaPath);
    			return imagemRedimensionadaPath;
    		}
    		
    		UtilFilesStorage.createDirectory(root);
    		BufferedImage image = ImageIO.read(new URL("https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=" + login));
    		if (image != null) {
    			UtilFilesStorage.saveImage(imagemRedimensionadaPath, image);
    			ImageResizer.resize(imagemRedimensionadaPath, imagemRedimensionadaPath, 0.5);
    			return imagemRedimensionadaPath;
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
     * Recupera o avatar do usuário pelo endpoint do cliente
     * https://www.dminerweb.com.br:8553/api/auth/avatar/?login_user=?
     * @param login
     * @return byte[]
     */
	@Deprecated
    public byte[] getAvatar2(String login) {
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
