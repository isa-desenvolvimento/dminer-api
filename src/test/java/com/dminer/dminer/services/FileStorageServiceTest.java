package com.dminer.dminer.services;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.dminer.dminer.repository.FilesDatabaseRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FileStorageServiceTest {
	
	@Autowired
	private FileStorageService filesStorageService;
	
	
	//@Before
	public void setUp() throws Exception {
		filesStorageService.init();
	}
	
	
	//@Test
	public void testCriarDiretorio() {
		filesStorageService.init();
		File f = new File(System.getProperty("user.dir") + "\\uploads");		
		assertTrue(f.exists());
	}
	
	//@Test
	public void testUploadImagem() {
		
	}
	
	
	
}
