// /*
//  * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
//  * Todos os direitos reservados.
//  */
// package com.dminer.controllers.autenticacao;


// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
// import io.swagger.annotations.ApiParam;
// import java.security.SecureRandom;
// import java.sql.Timestamp;
// import java.text.SimpleDateFormat;
// import java.time.Instant;
// import java.util.ArrayList;
// import java.util.Base64;
// import java.util.Date;
// import java.util.List;
// import java.util.Optional;

// import com.dminer.config.seguranca.TokenProvider;
// import com.dminer.dto.autenticacao.ApiKeyDTO;
// import com.dminer.entities.autenticacao.ApiKey;
// import com.dminer.repository.autenticacao.ApikeyRepository;
// import com.dminer.response.Response;
// import com.dminer.utils.UtilDataHora;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.domain.Specification;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.annotation.Secured;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.util.UriComponentsBuilder;

// /**
//  *
//  * @author Paulo Collares
//  */
// @RestController
// @CrossOrigin
// @Secured({"ROLE_ADA"})
// @RequestMapping("/autenticacao/apikey")
// @Api(tags = "API keys", description = "API keys")
// public class ApiKeyController {

//     private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

//     @Autowired
//     TokenProvider tokenProvider;

//     @Autowired
//     ApikeyRepository apikeyRepository;



//     @ApiOperation(value = "Lista as api keys")
//     @GetMapping(value = "/all")
//     public ResponseEntity<Response<List<ApiKey>>> listar() {
        
//         Response<List<ApiKey>> response = new Response<>();
//         Iterable<ApiKey> findAll = apikeyRepository.findAll();

//         if (! findAll.iterator().hasNext()) {
//             response.getErrors().add("Nenhuma api key encontrada");
//             return ResponseEntity.status(404).body(response);
//         }

//         List<ApiKey> apis = new ArrayList<>();
//         findAll.forEach(api -> {
//             apis.add(api);
//         });
//         response.setData(apis);
//         return ResponseEntity.ok().body(response);
//     }


//     @ApiOperation(value = "Busca uma api key pelo id")
//     @GetMapping(value = "/{id}")
//     public ResponseEntity<Response<ApiKey>> get(@PathVariable int id) {

//         Response<ApiKey> response = new Response<>();
//         Optional<ApiKey> api = apikeyRepository.findById(id);

//         if (!api.isPresent()) {
//             response.getErrors().add("Api key não encontrada");
//             return ResponseEntity.status(404).body(response);
//         }
//         response.setData(api.get());
//         return ResponseEntity.ok().body(response);
//     }


    
//     @ApiOperation(value = "Cria uma nova apikey")
//     @PostMapping()
//     public ResponseEntity<Response<ApiKey>> criar(@RequestBody ApiKeyDTO dto) {
        
//         Response<ApiKey> response = new Response<>();
//         try {
//             //Crio um objeto da entidade preenchendo com os valores do DTO e validando
//             ApiKey apiKey = new ApiKey();

//             if (dto.getHabilitado() == null) {
//                 apiKey.setHabilitado(true);
//             } else {
//                 apiKey.setHabilitado(dto.getHabilitado());
//             }

//             if (dto.getNome() == null || dto.getNome().isEmpty()) {
//                 response.getErrors().add("Informe o nome");
//                 return ResponseEntity.badRequest().body(response);                
//             }
//             apiKey.setNome(dto.getNome());

            
//             if (!dto.getDataVencimento().matches("\\d{4}-\\d{2}-\\d{2}")) {
//                 response.getErrors().add("Data inválida");
//                 return ResponseEntity.badRequest().body(response);
//             }
//             Timestamp data = UtilDataHora.toTimestamp(dto.getDataVencimento());
//             apiKey.setDataVencimento(data);
        
//             apiKey.setDataCriacao(Timestamp.from(Instant.now()));

//             apiKey.setKey(gerarNovaApiKey(apiKey));

//             //Salvo o objeto utilizando o repositório
//             ApiKey novo = apikeyRepository.save(apiKey);

//             //Se ocorreu algum erro, retorno esse erro para a API
//             if (novo == null) {
//                 response.getErrors().add("Erro na criação da api key");
//                 return ResponseEntity.badRequest().body(response);
//             }

//             response.setData(novo);
//             return ResponseEntity.ok().body(response);
//         } catch (Exception e) {
//             LOGGER.error("Erro ao criar uma apikey", e);
//             response.getErrors().add("Erro ao criar uma apikey");
//             return ResponseEntity.badRequest().body(response);            
//         }
//     }


//     @ApiOperation(value = "Deletar uma apikey")
//     @DeleteMapping()
//     public ResponseEntity<Response<Boolean>> delete(@PathVariable int id) {

//         Response<Boolean> response = new Response<>();
//         Optional<ApiKey> api = apikeyRepository.findById(id);

//         if (!api.isPresent()) {
//             response.getErrors().add("Api key não encontrada");
//             return ResponseEntity.status(404).body(response);
//         }

//         apikeyRepository.deleteById(id);
//         response.setData(true);
//         return ResponseEntity.ok().body(response);
//     }


//     private String gerarNovaApiKey(ApiKey apiKey) {
//         Base64.Encoder base64Encoder = Base64.getUrlEncoder();
//         SecureRandom secureRandom = new SecureRandom();

//         byte[] randomBytes = new byte[64];
//         secureRandom.nextBytes(randomBytes);

//         StringBuilder sb
//                 = new StringBuilder("aicare.apikey")
//                         .append(".")
//                         .append(apiKey.getNome().toLowerCase().replaceAll(" ", "_"))
//                         .append(".")
//                         .append(apiKey.getDataCriacao().getTime())
//                         .append(".")
//                         .append(base64Encoder.encodeToString(randomBytes));

//         return base64Encoder.encodeToString(sb.toString().getBytes());
//     }

// }
