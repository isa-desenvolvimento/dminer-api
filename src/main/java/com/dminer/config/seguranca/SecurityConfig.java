// /*
//  * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
//  * Todos os direitos reservados.
//  */
// package com.dminer.config.seguranca;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// /**
//  * Classe principal do controle de segurança
//  *
//  * @author Paulo Collares
//  */
// @Configuration
// @EnableGlobalMethodSecurity(securedEnabled  = false)
// public class SecurityConfig extends WebSecurityConfigurerAdapter {

//     @Autowired
//     private JwtAuthenticationEntryPoint unauthorizedHandler;

//     //WHITELIST de endereços que não serão autenticados
//     private static final String[] AUTH_WHITELIST = {
//         "/",
//         "/v2/api-docs",
//         "/swagger-resources",
//         "/swagger-resources/**",
//         "/configuration/ui",
//         "/configuration/security",
//         "/swagger-ui.html",
//         "/webjars/**"
//     };

//     @Bean
//     @Override
//     public AuthenticationManager authenticationManagerBean() throws Exception {
//         return super.authenticationManagerBean();
//     }

//     @Bean
//     public JWTAuthenticationFilter geAuthenticationFilter() {
//         return new JWTAuthenticationFilter();
//     }

//     @Override
//     protected void configure(HttpSecurity http) throws Exception {

//         http.csrf().disable().authorizeRequests()
//                 .antMatchers(AUTH_WHITELIST).permitAll()//Permite todo o acesso a whitelist
//                 .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() //Permite o acesso por options para todos
//                 .antMatchers(HttpMethod.POST, "/login").permitAll() //Permite o acesso por post ao /login
//                 .antMatchers(HttpMethod.GET, "/ping").permitAll() //Permite o acesso por get ao /ping
//                 .antMatchers(HttpMethod.GET, "/config/public").permitAll() //Permite o acesso por get ao /config/public
//                 .anyRequest().authenticated()
//                 .and()
//                 .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//                 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//         http
//                 .addFilterBefore(geAuthenticationFilter(),// Filtro todas as demais requisições para validar se o header contém o token JWT
//                         UsernamePasswordAuthenticationFilter.class);
//     }

// }
