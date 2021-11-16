// /*
//  * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
//  * Todos os direitos reservados.
//  */
// package br.com.aicare.di.api_rest.controladores.rest.autenticacao;

// import br.com.aicare.di.api_rest.uteis.ApiError;
// import br.com.aicare.di.api_rest.configuracoes.internacionalizacao.Translator;
// import br.com.aicare.di.api_rest.configuracoes.seguranca.TokenProvider;
// import br.com.aicare.di.api_rest.dominio.autenticacao.Perfil;
// import br.com.aicare.di.api_rest.dominio.autenticacao.Usuario;
// import br.com.aicare.di.api_rest.dominio.pessoal.pessoa.PessoaFisica;
// import br.com.aicare.di.api_rest.dto.autenticacao.AlterarSenhaDTO;
// import br.com.aicare.di.api_rest.dto.autenticacao.UsuarioDTO;
// import br.com.aicare.di.api_rest.repository.autenticacao.HistoricoLoginRepository;
// import br.com.aicare.di.api_rest.repository.autenticacao.PerfilRepository;
// import br.com.aicare.di.api_rest.repository.autenticacao.UsuarioRepository;
// import br.com.aicare.di.api_rest.repository.pessoal.PessoaFisicaRepository;
// import br.com.aicare.di.api_rest.uteis.PageableFactory;
// import br.com.aicare.di.api_rest.uteis.Senha;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Optional;
// import java.util.Set;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.annotation.Secured;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.util.UriComponentsBuilder;

// /**
//  *
//  * @author FERNANDA
//  */
// @RestController
// @CrossOrigin
// @RequestMapping("/autenticacao/usuario")
// @Api(tags = "Usuário", description = "Usuário")
// public class UsuarioController {

//     private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

//     @Autowired
//     TokenProvider tokenProvider;

//     @Autowired
//     UsuarioRepository usuarioRepository;

//     @Autowired
//     PessoaFisicaRepository pessoaFisicaRepository;

//     @Autowired
//     PerfilRepository perfilRepository;
    
//     @Autowired
//     HistoricoLoginRepository historicoLoginRepository;

//     @ApiOperation(value = "Lista os usuarios")
//     @GetMapping()
//     public Page<Usuario> listar(
//             @RequestParam(
//                     value = "page",
//                     required = false,
//                     defaultValue = "0") int page,
//             @RequestParam(
//                     value = "size",
//                     required = false,
//                     defaultValue = "10") int size,
//             @RequestParam(
//                     value = "sort",
//                     required = false) String sort,
//             @RequestParam(
//                     value = "q",
//                     required = false) String q
//     ) {
//         Pageable pageable = new PageableFactory(page, size, sort).getPageable();

//         Page<Usuario> resultPage;

//         if (q == null) {
//             resultPage = usuarioRepository.findAll(pageable);
//         } else {
//             resultPage = usuarioRepository.busca(q.toLowerCase(), pageable);
//         }

//         return resultPage;
//     }

//     @GetMapping(value = "/perfil")
//     public ResponseEntity<Usuario> perfil(@RequestHeader(name = "Authorization") String token) {
//         if (token == null) {
//             return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//         }
//         try {
//             String username = tokenProvider.getUserFromToken(token);

//             Optional<Usuario> usuario = usuarioRepository.findByLogin(username);

//             if (!usuario.isPresent()) {
//                 return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//             }

//             return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
//         } catch (Exception e) {
//             LOGGER.error("Erro retornar o usuário logado", e);
//             return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//         }
//     }

//     @GetMapping(value = "/meus-perfis")
//     public ResponseEntity<Set<Perfil>> perfis(@RequestHeader(name = "Authorization") String token) {
//         if (token == null) {
//             return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//         }
//         try {
//             String username = tokenProvider.getUserFromToken(token);

//             Optional<Usuario> usuario = usuarioRepository.findByLogin(username);

//             if (!usuario.isPresent()) {
//                 return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//             }

//             return new ResponseEntity<>(usuario.get().getPerfis(), HttpStatus.OK);
//         } catch (Exception e) {
//             LOGGER.error("Erro retornar o usuário logado", e);
//             return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//         }
//     }

//     @PutMapping(value = "/senha")
//     public ResponseEntity<Usuario> alterarSenha(@RequestBody AlterarSenhaDTO dto, @RequestHeader(name = "Authorization") String token) {
//         if (token == null) {
//             return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//         }
//         try {
//             String username = tokenProvider.getUserFromToken(token);

//             Optional<Usuario> usuario = usuarioRepository.findByLogin(username);

//             if (!usuario.isPresent()) {
//                 return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//             }

//             if (dto.getSenhaAtual() == null || dto.getSenhaAtual().isEmpty()) {
//                 return ApiError.notFound(Translator.toLocale("validacao_alterar_senha_informe_senha_atual"));
//             }

//             Senha senha = new Senha(dto.getSenhaAtual());

//             if (!senha.getHash().equals(usuario.get().getSenha())) {
//                 return ApiError.unauthorized(Translator.toLocale("validacao_alterar_senha_erro_senha_atual"));
//             }

//             if (dto.getSenhaNova() == null || dto.getSenhaNova().length() < 8 || dto.getSenhaNova().length() > 160) {
//                 return ApiError.notFound(Translator.toLocale("validacao_alterar_senha_informe_senha_nova"));
//             }
//             if (dto.getSenhaNova2() == null || dto.getSenhaNova2().isEmpty()) {
//                 return ApiError.notFound(Translator.toLocale("validacao_alterar_senha_informe_senha_nova2"));
//             }
//             if (!dto.getSenhaNova().equals(dto.getSenhaNova2())) {
//                 return ApiError.notFound(Translator.toLocale("validacao_alterar_senha_senhas_diferentes"));
//             }

//             Senha senhaNova = new Senha(dto.getSenhaNova());
//             usuario.get().setSenha(senhaNova.getHash());

//             //Atualizo o objeto utilizando o repositório
//             Usuario atualizado = usuarioRepository.save(usuario.get());

//             //Se ocorreu algum erro, retorno esse erro para a API
//             if (atualizado == null) {
//                 return ApiError.internalServerError(Translator.toLocale("erro_atualizacao"));
//             }

//             //Se foi criado com sucesso, retorno o objeto atualizado
//             return new ResponseEntity<>(atualizado, HttpStatus.CREATED);

//         } catch (Exception e) {
//             LOGGER.error("Erro retornar o usuário logado", e);
//             return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//         }
//     }

//     @ApiOperation(value = "Busca um usuario pelo id")
//     @GetMapping(value = "/{id}")
//     public ResponseEntity<Usuario> listar(@PathVariable Integer id, @RequestHeader(name = "Authorization") String token) {
//         Optional<Usuario> usuario = usuarioRepository.findById(id);

//         if (!usuario.isPresent()) {
//             return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//         }

//         int meuMaiorNivelPerfil = getMeuMaiorNivelPerfil(token);
//         for (Perfil p : usuario.get().getPerfis()) {
//             if (p.getHierarquia() > meuMaiorNivelPerfil) {
//                 return ApiError.unauthorized(Translator.toLocale("perfil_maior_hierarquia_usuario_nao_possivel_editar"));
//             }
//         }

//         return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
//     }

//     @ApiOperation(value = "Lista os perfis de usuário")
//     @GetMapping(value = "/perfis")
//     public ResponseEntity<List<Perfil>> listar(@RequestHeader(name = "Authorization") String token) {

//         int meuMaiorNivelPerfil = getMeuMaiorNivelPerfil(token);

//         List<Perfil> perfis = new ArrayList<>();

//         for (Perfil p : perfilRepository.findAll()) {
//             if (p.getHierarquia() <= meuMaiorNivelPerfil) {
//                 perfis.add(p);
//             }
//         }

//         return new ResponseEntity<>(perfis, HttpStatus.OK);
//     }

//     @Secured({"ROLE_ADA", "ROLE_ADM", "ROLE_ADH"})
//     @ApiOperation(value = "Cria um novo usuario")
//     @PostMapping()
//     public ResponseEntity<Usuario> criar(@RequestBody UsuarioDTO dto, UriComponentsBuilder ucBuilder, @RequestHeader(name = "Authorization") String token) {

//         try {

//             int meuMaiorNivelPerfil = getMeuMaiorNivelPerfil(token);

//             //Crio um objeto da entidade preenchendo com os valores do DTO e validando
//             Usuario usuario = new Usuario();

//             if (dto.isHabilitado() == null) {
//                 usuario.setHabilitado(true);
//             } else {
//                 usuario.setHabilitado(dto.isHabilitado());
//             }

//             if (dto.getLogin() == null || dto.getLogin().length() < 3 || !dto.getLogin().matches("[A-Za-z0-9_]+")) {
//                 return ApiError.badRequest(Translator.toLocale("login_invalido"));
//             }
//             if (usuarioRepository.loginExistente(dto.getLogin().toLowerCase())) {
//                 return ApiError.badRequest(Translator.toLocale("login_ja_cadastrado") + ": " + dto.getLogin());
//             }
//             usuario.setLogin(dto.getLogin());

//             if (dto.getSenha() == null || dto.getSenha().length() < 8 || dto.getSenha().length() > 160) {
//                 return ApiError.badRequest(Translator.toLocale("validacao_senha"));
//             }

//             Senha senha = new Senha(dto.getSenha());
//             usuario.setSenha(senha.getHash());

//             usuario.setDataCriacao(new Date());

//             Optional<PessoaFisica> pessoa = pessoaFisicaRepository.findById(dto.getIdPessoa());
//             if (!pessoa.isPresent()) {
//                 return ApiError.badRequest(Translator.toLocale("validacao_usuario_pessoa") + ": " + dto.getIdPessoa());
//             }
//             Optional<Usuario> usuarioPessoa = usuarioRepository.findByPessoa(pessoa.get());
//             if (usuarioPessoa.isPresent()) {
//                 return ApiError.badRequest(Translator.toLocale("validacao_pessoa_ja_possui_usuario"));
//             }
//             usuario.setPessoa(pessoa.get());

//             usuario.setPerfis(new HashSet<>());
//             if (dto.getPerfis() != null) {
//                 for (String p : dto.getPerfis()) {
//                     Optional<Perfil> perfil = perfilRepository.findByCodigo(p);
//                     if (perfil.isPresent()) {
//                         if (perfil.get().getHierarquia() > meuMaiorNivelPerfil) {
//                             return ApiError.badRequest(Translator.toLocale("perfil_maior_hierarquia_usuario") + ": " + perfil.get().getNome());
//                         }
//                         usuario.getPerfis().add(perfil.get());
//                     }
//                 }
//             }
//             if (usuario.getPerfis().isEmpty()) {
//                 return ApiError.internalServerError(Translator.toLocale("informe_pelo_menos_um_perfil"));
//             }

//             //Salvo o objeto utilizando o repositório
//             Usuario novo = usuarioRepository.save(usuario);

//             //Se ocorreu algum erro, retorno esse erro para a API
//             if (novo == null) {
//                 return ApiError.badRequest(Translator.toLocale("erro_criacao"));
//             }

//             //Se foi criado com sucesso, retorno o objeto criado
//             return new ResponseEntity<>(novo, HttpStatus.CREATED);
//         } catch (Exception e) {
//             LOGGER.error("Erro ao criar um usuario", e);
//             return ApiError.internalServerError(Translator.toLocale("erro_criacao"));
//         }
//     }

//     @Secured({"ROLE_ADA", "ROLE_ADM", "ROLE_ADH"})
//     @ApiOperation(value = "Atualiza um usuario")
//     @PutMapping(value = "/{id}")
//     public ResponseEntity<Usuario> atualizar(@PathVariable("id") int id, @RequestBody UsuarioDTO dto, @RequestHeader(name = "Authorization") String token) {
//         try {

//             int meuMaiorNivelPerfil = getMeuMaiorNivelPerfil(token);

//             Optional<Usuario> usuarioAtual = usuarioRepository.findById(id);

//             if (!usuarioAtual.isPresent()) {
//                 return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//             }

//             if (dto.isHabilitado() != null) {
//                 usuarioAtual.get().setHabilitado(dto.isHabilitado());
//             }

//             if (dto.getLogin() != null) {
//                 if (dto.getLogin().length() < 3 || !dto.getLogin().matches("[A-Za-z0-9_]+")) {
//                     return ApiError.badRequest(Translator.toLocale("login_invalido"));
//                 }
//                 if (!usuarioAtual.get().getLogin().equals(dto.getLogin()) && usuarioRepository.loginExistente(dto.getLogin().toLowerCase())) {
//                     return ApiError.badRequest(Translator.toLocale("login_ja_cadastrado") + ": " + dto.getLogin());
//                 }
//                 usuarioAtual.get().setLogin(dto.getLogin());
//             }

//             if (dto.getSenha() != null) {
//                 if (dto.getSenha().length() < 8 || dto.getSenha().length() > 160) {
//                     return ApiError.badRequest(Translator.toLocale("validacao_senha"));
//                 }

//                 Senha senha = new Senha(dto.getSenha());
//                 usuarioAtual.get().setSenha(senha.getHash());

//             }

//             if (dto.getPerfis() != null) {
//                 usuarioAtual.get().getPerfis().clear();
//                 for (String p : dto.getPerfis()) {
//                     Optional<Perfil> perfil = perfilRepository.findByCodigo(p);
//                     if (perfil.isPresent()) {
//                         if (perfil.get().getHierarquia() > meuMaiorNivelPerfil) {
//                             return ApiError.badRequest(Translator.toLocale("perfil_maior_hierarquia_usuario") + ": " + perfil.get().getNome());
//                         }
//                         usuarioAtual.get().getPerfis().add(perfil.get());
//                     }
//                 }
//             }

//             if (usuarioAtual.get().getPerfis().isEmpty()) {
//                 return ApiError.internalServerError(Translator.toLocale("informe_pelo_menos_um_perfil"));
//             }

//             //Atualizo o objeto utilizando o repositório
//             Usuario atualizado = usuarioRepository.save(usuarioAtual.get());

//             //Se ocorreu algum erro, retorno esse erro para a API
//             if (atualizado == null) {
//                 return ApiError.internalServerError(Translator.toLocale("erro_atualizacao"));
//             }

//             //Se foi criado com sucesso, retorno o objeto atualizado
//             return new ResponseEntity<>(atualizado, HttpStatus.CREATED);
//         } catch (Exception e) {
//             LOGGER.error("Erro ao atualizar um usuário", e);
//             return ApiError.internalServerError(Translator.toLocale("erro_atualizacao"));
//         }
//     }

//     private int getMeuMaiorNivelPerfil(String token) {
//         int nivel = -1;

//         String username = tokenProvider.getUserFromToken(token);

//         Optional<Usuario> usuario = usuarioRepository.findByLogin(username);

//         if (usuario.isPresent()) {
//             for (Perfil p : usuario.get().getPerfis()) {
//                 if (p.getHierarquia() > nivel) {
//                     nivel = p.getHierarquia();
//                 }
//             }
//         }

//         return nivel;
//     }

//     @ApiOperation(value = "Remove um usuario pelo id")
//     @DeleteMapping(value = "/{id}")
//     @Transactional
//     public ResponseEntity<Usuario> remover(@PathVariable Integer id) {
//         Optional<Usuario> registro = usuarioRepository.findById(id);

//         if (!registro.isPresent()) {
//             return ApiError.notFound(Translator.toLocale("registro_nao_encontrado"));
//         }
 
//         historicoLoginRepository.deleteByUsuario(registro.get());
//         usuarioRepository.delete(registro.get());

//         LOGGER.info("Usuário removido: " + registro.get().getId());

//         return new ResponseEntity<>(null, HttpStatus.OK);
//     }

// }
