// /*
//  * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
//  * Todos os direitos reservados.
//  */
// package com.dminer.config.seguranca;

// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.MalformedJwtException;
// import io.jsonwebtoken.SignatureException;
// import io.jsonwebtoken.UnsupportedJwtException;
// import java.io.IOException;
// import javax.servlet.FilterChain;
// import javax.servlet.ServletException;
// import javax.servlet.ServletResponse;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.filter.OncePerRequestFilter;

// /**
//  * Filtra as requisições que devem conter o token JWT no header
//  *
//  * @author Paulo Collares
//  */
// public class JWTAuthenticationFilter extends OncePerRequestFilter {

//     private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

//     @Autowired
//     TokenProvider tokenProvider;

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

//         Authentication authentication = null;
//         try {
//             authentication = tokenProvider.getAuthentication(request, response);
//         } catch (ExpiredJwtException exception) {
//             sendError("Token JWT expirado", HttpStatus.UNAUTHORIZED, response);
//             LOGGER.error("Token JWT expirado", exception);
//             return;
//         } catch (UnsupportedJwtException | SignatureException | IllegalArgumentException | MalformedJwtException exception) {
//             sendError("Token JWT inválido", HttpStatus.UNAUTHORIZED, response);
//             LOGGER.error("Token JWT inválido", exception);
//             return;
//         }

//         SecurityContextHolder.getContext().setAuthentication(authentication);
//         filterChain.doFilter(request, response);
//     }

//     private void sendError(String mensagem, HttpStatus httpStatus, ServletResponse response) throws IOException {
//         String serialized = httpStatus + " - " + mensagem;
//         ((HttpServletResponse) response).setHeader("Content-Type", "application/json");
//         ((HttpServletResponse) response).setStatus(httpStatus.value());
//         response.getOutputStream().write(serialized.getBytes());
//     }

// }
