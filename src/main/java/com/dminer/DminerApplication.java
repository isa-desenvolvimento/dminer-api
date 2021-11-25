package com.dminer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DminerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DminerApplication.class, args);
	}

	// Long currentTimeMillis = System.currentTimeMillis**();**
	// System.out.println(currentTimeMillis); // 1617752114615
	// Date novaData = new Date(currentTimeMillis);
	// System.out.println(novaData); // Tue Apr 06 20:36:02 BRT 2021
}