package com.dminer.components;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



public class TokenService {
    

    private static String token;

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    TokenService() {
        afterPropertiesSet();
    }

    private void afterPropertiesSet() {
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
        token =  (String) retorno.get("baererAuthentication");
    }

    public static String getToken() {
        if (token == null) {
            log.info("Requisitando novo token do endpoint: https://www.dminerweb.com.br:8553/api/auth/login");
            new TokenService();
        }
        log.info("Token do endpoint: https://www.dminerweb.com.br:8553/api/auth/login");
        log.info(token);
        return token;
    }
    
}
