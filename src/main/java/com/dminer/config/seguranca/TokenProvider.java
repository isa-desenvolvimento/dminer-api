// /*
//  * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
//  * Todos os direitos reservados.
//  */
// package com.dminer.config.seguranca;

// import com.dminer.entities.autenticacao.*;
// import com.dminer.repository.UserRepository;
// import com.dminer.repository.autenticacao.*;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.Jws;
// import io.jsonwebtoken.Jwts;
// import java.util.Date;
// import javax.servlet.http.HttpServletRequest;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.UnsupportedJwtException;
// import java.io.IOException;
// import java.io.Serializable;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;
// import javax.servlet.http.HttpServletResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
// import org.springframework.stereotype.Component;

// /**
//  * Classe que gerencia o token JWT
//  *
//  * @author Paulo Collares
//  */
// @Component
// public class TokenProvider implements Serializable {

//     @Value("${jwt.secret}")
//     private String secret;

//     private static final long EXPIRATION_TIME = 3 * 60 * 60 * 1000;
//     public static final String TOKEN_PREFIX = "Bearer";
//     private static final String HEADER_STRING = "Authorization";
//     private static final String AUTHORITIES_KEY = "authorities";

//     @Autowired
//     ApikeyRepository apikeyRepository;

//     @Autowired
//     UserRepository usuarioRepository;

//     public String createToken(String subject, String authoritie) throws JsonProcessingException {

//         Date now = new Date();
//         Date exp = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

//         String JWT = Jwts.builder()
//                 .setSubject(subject)
//                 .setIssuer("aicare")
//                 .setIssuedAt(now)
//                 .setExpiration(exp)
//                 .claim(AUTHORITIES_KEY, authoritie)
//                 .signWith(SignatureAlgorithm.HS512, secret)
//                 .compact();
//         return JWT;
//     }

//     public UsernamePasswordAuthenticationToken getAuthentication(final String token) {

//         Jws<Claims> jws = Jwts.parser()
//                 .setSigningKey(secret)
//                 .parseClaimsJws(token.replace(TOKEN_PREFIX, ""));

//         if (jws == null) {
//             return null;
//         }

//         String subject = jws.getBody().getSubject();
//         String claims = jws.getBody().get(AUTHORITIES_KEY).toString();

//         List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//         String[] p = claims.split(",");
//         for (String a : p) {
//             authorities.add(new SimpleGrantedAuthority("ROLE_" + a));
//         }

//         return new UsernamePasswordAuthenticationToken(subject, "", authorities);
//     }

//     public String getUserFromToken(String token) {
//         UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(token);
//         if (usernamePasswordAuthenticationToken != null) {
//             return usernamePasswordAuthenticationToken.getName();
//         }
//         return null;
//     }

//     public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {

//         String token = request.getHeader(HEADER_STRING);

//         if (token != null) {

//             if (token.contains(TOKEN_PREFIX)) {  //Token JWT
//                 UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(token);
//                 if (usernamePasswordAuthenticationToken != null) {
//                     boolean habilitado = usuarioRepository.isHabilitado(usernamePasswordAuthenticationToken.getName());
//                     if (habilitado) {
//                         return usernamePasswordAuthenticationToken;
//                     } else {
//                         throw new ExpiredJwtException(null, null, "Este usuário foi desabilitado");
//                     }
//                 }
//             } else {//API KEY
//                 ApiKey apiKey = apikeyRepository.findByKey(token);
//                 if (apiKey == null) {
//                     throw new UnsupportedJwtException("Api key inexistente");
//                 }
//                 if (apiKey.isHabilitado()) {
//                     if (apiKey.getDataVencimento().after(new Date())) {
//                         List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//                         authorities.add(new SimpleGrantedAuthority("ROLE_EMPTY"));
//                         authorities.add(new SimpleGrantedAuthority("ROLE_" + apiKey.getPerfil().getDescrible().toUpperCase()));

//                         return new PreAuthenticatedAuthenticationToken(token, "", authorities);
//                     } else {
//                         throw new ExpiredJwtException(null, null, "Api key expirada");
//                     }
//                 } else {
//                     throw new ExpiredJwtException(null, null, "Api key desabilitada");
//                 }
//             }

//         }
//         return null;
//     }

// }
