package com.dminer.dminer.repository;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.dminer.dminer.entities.FileInfo;
import com.dminer.dminer.entities.Post;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FilesDatabaseRepositoryTest {

	@Autowired
	private FilesDatabaseRepository filesStorageRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	private static final String CONTENT_POST = "Loren Ipsum";
	
	
	@Before
	public void setUp() throws Exception {
		Post post = new Post();
		post.setId(1);
		post.setContent(CONTENT_POST);
		this.postRepository.save(post);
	}
	
	@Test
	public void testFile() {
		Optional<Post> post = this.postRepository.findById(1);
		if (post.isPresent()) {
			FileInfo foto = new FileInfo();			
			foto.setUrl("");
			foto.setPost(post.get());
			
			foto = this.filesStorageRepository.save(foto);
			System.out.println(foto.toString());
			
		}	
	}
	
}
