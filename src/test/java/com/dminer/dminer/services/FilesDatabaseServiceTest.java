package com.dminer.dminer.services;

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
public class FilesDatabaseServiceTest {

	@MockBean
	private FilesDatabaseRepository filesStorageRepository;
	
	@Autowired
	private FileStorageService filesStorageService;
	
	//@Before
	public void setUp() throws Exception {
//		FileInfo p = new FileInfo();
//		p.setPost(new Post(1, ""));
//		
//		List<Archive> archs = new ArrayList<>();
//		archs.add(p);
//		archs.add(v);
//		archs.add(d);
//		
//		BDDMockito.given(
//			this.filesStorageRepository.findByPost(Mockito.any(Post.class))
//		).willReturn(Optional.of(archs));
//		
//		BDDMockito.given(this.filesStorageRepository.save(Mockito.any(Photo.class))).willReturn(p);
//		BDDMockito.given(this.filesStorageRepository.findById(Mockito.anyInt())).willReturn(Optional.of(p));
	}
	
	
	//@Test
	public void testBuscarArquivosPorPost() {
//		Post post = new Post();
//		Optional<List<Archive>> arquivos = this.filesStorageService.findByPost(post);		
//		assertTrue(arquivos.isPresent());
	}
	
	//@Test
	public void testPersistirArquivos() {
//		Photo p = new Photo(new Post(1));
//		Photo p2 = (Photo) this.filesStorageService.persist(p);
//		assertNotNull(p2);
	}
	
	//@Test
	public void testFindIdArquivos() {
//		Photo p = new Photo(1);
//		Optional<Archive> p2 = this.filesStorageService.findById(1);
//		assertTrue(p2.isPresent());
	}
}
