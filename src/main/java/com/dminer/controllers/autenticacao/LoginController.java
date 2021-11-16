// /*
//  * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
//  * Todos os direitos reservados.
//  */
// package com.dminer.controllers.autenticacao;

// import static org.springframework.http.ResponseEntity.ok;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// import javax.servlet.http.HttpServletRequest;

// import com.dminer.config.seguranca.TokenProvider;
// import com.dminer.dto.LoginDTO;
// import com.dminer.dto.UserTokenDTO;
// import com.dminer.entities.Profile;
// import com.dminer.entities.User;
// import com.dminer.repository.UserRepository;
// import com.dminer.response.Response;
// import com.dminer.utils.Senha;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// // import br.com.aicare.di.api_rest.configuracoes.internacionalizacao.Translator;
// // import br.com.aicare.di.api_rest.configuracoes.seguranca.TokenProvider;
// // import br.com.aicare.di.api_rest.dominio.autenticacao.HistoricoLogin;
// // import br.com.aicare.di.api_rest.dominio.autenticacao.Perfil;
// // import br.com.aicare.di.api_rest.dominio.autenticacao.User;
// // import br.com.aicare.di.api_rest.dominio.pessoal.colaborador.VinculoTrabalho;
// // import br.com.aicare.di.api_rest.dominio.pessoal.pessoa.PessoaFisica;
// // import br.com.aicare.di.api_rest.dto.autenticacao.LoginDTO;
// // import br.com.aicare.di.api_rest.repository.autenticacao.HistoricoLoginRepository;
// // import br.com.aicare.di.api_rest.repository.autenticacao.UsuarioRepository;
// // import br.com.aicare.di.api_rest.repository.pessoal.PessoaFisicaRepository;
// // import br.com.aicare.di.api_rest.repository.pessoal.VinculoTrabalhoRepository;
// // import br.com.aicare.di.api_rest.uteis.ApiError;
// // import br.com.aicare.di.api_rest.uteis.Senha;

// /**
//  *
//  * @author Paulo Collares
//  */
// @RestController
// @CrossOrigin
// @RequestMapping("/login")
// public class LoginController {
    
//     private final Logger log = LoggerFactory.getLogger(LoginController.class);
    
//     @Autowired
//     TokenProvider tokenProvider;
    
//     @Autowired
//     UserRepository usuarioRepository;
    
    
//     @PostMapping()
//     public ResponseEntity<Response<UserTokenDTO>> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        
//         log.info("Efetuando login com {}", dto.getUsername());
//         Response<UserTokenDTO> response = new Response<>();
        
//         Optional<User> user = null;
//         String userName = dto.getUsername();
        

//         try {
//             if (userName == null || userName.isEmpty()) {
//                 response.getErrors().add("Informe seu login");
//                 return ResponseEntity.badRequest().body(response);
//             }
//             if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
//                 response.getErrors().add("Informe sua senha");
//                 return ResponseEntity.badRequest().body(response);
//             }
            

//             //Tento logar pelo login
//             log.info("Buscando usuário pelo login: {}", userName);
//             user = usuarioRepository.findByLogin(userName);
            
//             //Se não encontrar, tento pelo CPF da pessoa associada
//             if (!user.isPresent()) {
//                 response.getErrors().add("Login não encontrado");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             if (user == null || !user.isPresent()) {
//                 response.getErrors().add("Login inválido");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             User usuario = user.get();
            
//             log.info("Tratando senha...");
//             Senha senha = new Senha(dto.getPassword());
            
//             System.out.println("\n\nhash senha: " + senha.getHash());

//             if (!senha.getHash().equals(usuario.getSenha())) {
//                 response.getErrors().add("Senha inválida");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             if (usuario.getHabilitado() == null || usuario.getHabilitado() == false) {
//                 response.getErrors().add("Usuário desabilitado");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             String token = tokenProvider.createToken(usuario.getLogin(), usuario.getProfile().getDescrible());
//             log.info("Criando token de acesso com login = {} e perfil = {}", usuario.getLogin(), usuario.getProfile().getDescrible());

//             UserTokenDTO userToken = new UserTokenDTO();
//             userToken.setUsername(dto.getUsername());
//             userToken.setToken(token);
//             userToken.setType(TokenProvider.TOKEN_PREFIX);
            
//             log.info("Token criado com sucesso para o login: {}", user.get().getLogin());
//             response.setData(userToken);
//             return ResponseEntity.ok().body(response);

//         } catch (Exception e) {
//             log.error("Erro ao solicitar login", e);
//             response.getErrors().add("Erro ao solicitar login");
//             return ResponseEntity.internalServerError().body(response);
//         }
//     }        
// }
