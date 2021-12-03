package com.dminer.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.stereotype.Service;

import com.dminer.rest.model.permission.ConfigRestModel;
import com.dminer.rest.model.users.UserRestModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class PermissionService {

	
	public ConfigRestModel carregarPermissoesApi(String token) {
    	
    	String uri = "https://www.dminerweb.com.br:8553/api/administrative/client_area/user/list_general_permissions";		
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
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			ConfigRestModel staff = gson.fromJson(response, ConfigRestModel.class);
			return staff;
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        return null;
    }

	
}
