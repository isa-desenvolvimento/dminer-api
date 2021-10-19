package com.dminer.dminer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.dminer.dminer.entities.Document;
import com.dminer.dminer.entities.Photo;
import com.dminer.dminer.entities.Post;
import com.dminer.dminer.entities.Video;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ArchiveRepositoryTest {

	@Autowired
	private ArchiveRepository archiveRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	private static final String CONTENT_POST = "Loren Ipsum";
	private static final String FOTO_PATH = "C:/caminho-arquivo/foto/foto.jpeg";
	private static final String ARQUIVO_PATH = "C:/caminho-arquivo/arquivos/arquivo.txt";
	private static final String VIDEO_PATH = "C:/caminho-arquivo/video/video.mp4";
	
	
	@Before
	public void setUp() throws Exception {
		Post post = new Post();
		post.setId(1);
		post.setContent(CONTENT_POST);
		this.postRepository.save(post);
	}
	
	@Test
	public void testFoto() {
		Optional<Post> post = this.postRepository.findById(1);
		if (post.isPresent()) {
			Photo foto = new Photo();			
			foto.setPath(FOTO_PATH);
			foto.setPost(post.get());
			
			foto = this.archiveRepository.save(foto);
			System.out.println(foto.toString());
			
		    assertEquals(FOTO_PATH, foto.getPath());
		    assertEquals(post.get(), foto.getPost());
		}	
	}
	
	@Test
	public void testVideo() {
		Optional<Post> post = this.postRepository.findById(1);
		if (post.isPresent()) {
			Video video = new Video();			
			video.setPath(VIDEO_PATH);
			video.setPost(post.get());
			
			video = this.archiveRepository.save(video);
			System.out.println(video.toString());
			
		    assertEquals(VIDEO_PATH, video.getPath());
		    assertEquals(post.get(), video.getPost());
		}
	}
	
	@Test
	public void testDocumento() {
		Optional<Post> post = this.postRepository.findById(1);
		if (post.isPresent()) {
			Document documento = new Document();			
			documento.setPath(ARQUIVO_PATH);
			documento.setPost(post.get());
			
			documento = this.archiveRepository.save(documento);
			System.out.println(documento.toString());
			
		    assertEquals(ARQUIVO_PATH, documento.getPath());
		    assertEquals(post.get(), documento.getPost());
		}
	}
}
