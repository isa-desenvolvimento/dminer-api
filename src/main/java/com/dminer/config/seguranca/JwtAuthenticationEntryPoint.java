// /*
//  * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
//  * Todos os direitos reservados.
//  */
// package com.dminer.config.seguranca;

// import java.io.IOException;
// import java.io.Serializable;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.web.AuthenticationEntryPoint;
// import org.springframework.stereotype.Component;

// /**
//  *
//  * @author Paulo Collares
//  */
// @Component
// public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

//     @Override
//     public void commence(HttpServletRequest request,
//             HttpServletResponse response,
//             AuthenticationException authException) throws IOException {

//         response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//     }
// }
